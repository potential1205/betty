package org.example.betty.domain.game.service;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.repository.GamesRepository;
import org.example.betty.external.game.scraper.LineupScraper;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class GameCacheServiceImpl implements GameCacheService {

    private final GamesRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LineupAsyncExecutor lineupAsyncExecutor;
    private final RelayAsyncExecutor relayAsyncExecutor;

    public static final String REDIS_GAME_PREFIX = "games:";

    public GameCacheServiceImpl(GamesRepository gameRepository,
                                LineupScraper lineupScraper,
                                TaskScheduler taskScheduler,
                                RedisTemplate<String, Object> redisTemplate,
                                LineupAsyncExecutor lineupAsyncExecutor,
                                RelayAsyncExecutor relayAsyncExecutor) {
        this.gameRepository = gameRepository;
        this.lineupScraper = lineupScraper;
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
        this.lineupAsyncExecutor = lineupAsyncExecutor;
        this.relayAsyncExecutor = relayAsyncExecutor;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cacheDailyGames() {
        LocalDate today = LocalDate.now();

        List<Games> todayGames = gameRepository.findByGameDate(today);
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        int index = 0;

        for (Games game : todayGames) {
            String gameId = generateGameId(game);
            String redisKey = REDIS_GAME_PREFIX + today + ":" + gameId;

            RedisGameSchedule gameSchedule = RedisGameSchedule.builder()
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeam(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeam(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();

            hashOps.put(redisKey, "gameInfo", gameSchedule);
            hashOps.put(redisKey, "lineup", null);
            hashOps.put(redisKey, "relay", null);
            hashOps.put(redisKey, "seleniumIndex", index % 5);

            LocalDateTime expireTime = LocalDateTime.of(today, LocalTime.MAX);
            Date expireDate = Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant());
            redisTemplate.expireAt(redisKey, expireDate);

            if (!"CANCELED".equalsIgnoreCase(game.getStatus()) &&
                    !"ENDED".equalsIgnoreCase(game.getStatus())) {
                scheduleLineupJob(game);   // 라인업 예약
//                scheduleRelayJob(game);    // 중계 예약
            }

            index++;
        }
    }

    @Override
    @Transactional
    public boolean recoverTodayGameSchedule(LocalDate targetDate) {
        List<Games> todayGames = gameRepository.findByGameDate(targetDate);
        log.info("[복구] 오늘 경기 {}개 → 캐시 및 스케줄링 재시도", todayGames.size());
        cacheDailyGames();
        return true;
    }

    /**
     * Lineup 크롤링 예약: 경기 시작 30분 전 또는 이미 지났으면 즉시 실행
     */
    private void scheduleLineupJob(Games game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
        LocalDateTime gameStartDateTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());
        LocalDateTime executeTime = gameStartDateTime.minusMinutes(30);

        // 테스트용: 지금부터 10초 뒤에 실행하고 싶을 때
//        LocalDateTime executeTime = LocalDateTime.now().plusSeconds(10);

        Integer seleniumIndex = (Integer) redisTemplate.opsForHash().get(redisKey, "seleniumIndex");
        if (seleniumIndex == null) {
            log.warn("[라인업 크롤링 취소] seleniumIndex 없음 - gameId: {}", gameId);
            return;
        }

        Runnable task = () -> {
//            RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId);
            RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId, seleniumIndex);
            if (lineup != null) {
                redisTemplate.opsForHash().put(redisKey, "lineup", lineup);
                log.info("[라인업 저장 완료] - gameId: {}", gameId);
            } else {
                log.warn("[라인업 저장 실패] - gameId: {}", gameId);
            }
        };

        if (executeTime.isBefore(LocalDateTime.now())) {
            lineupAsyncExecutor.runAsync(task);  // 비동기로 즉시 실행
        } else {
            taskScheduler.schedule(
                    () -> lineupAsyncExecutor.runAsync(task),
                    executeTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[라인업 크롤링 예약] 경기 시작 30분 전 - gameId: {}", gameId);
        }
    }



    /**
     * Relay 크롤링 예약: Lineup이 저장된 후에 실행
     */
    private void scheduleRelayJob(Games game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
//        LocalDateTime gameStartTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());

        // 테스트용
        LocalDateTime gameStartTime = LocalDateTime.now().plusMinutes(2);


        final Integer seleniumIndex = (Integer) redisTemplate.opsForHash().get(redisKey, "seleniumIndex");
        if (seleniumIndex == null) {
            log.warn("[중계 크롤링 취소] seleniumIndex 없음 - gameId: {}", gameId);
            return;
        }

        if (gameStartTime.isBefore(LocalDateTime.now())) {
            relayAsyncExecutor.startRelay(gameId, seleniumIndex);
            log.info("[중계 크롤링 시작] 경기 시작 시간 지남 - gameId: {}", gameId);
        } else {
            taskScheduler.schedule(
                    () -> {
                        relayAsyncExecutor.startRelay(gameId, seleniumIndex);
                        log.info("[중계 크롤링 시작] 예약 실행 - gameId: {}", gameId);
                    },
                    gameStartTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[중계 크롤링 예약] 경기 시작 전 - gameId: {}, 시각: {}", gameId, gameStartTime);
        }
        scheduleRelayStopJob(game);
    }

    private void scheduleRelayStopJob(Games game) {
        String gameId = generateGameId(game);
        LocalDateTime stopTime = LocalDateTime.now().plusMinutes(2); // ⏱ 테스트용: 2분 뒤 종료
//  LocalDateTime stopTime = LocalDateTime.of(game.getGameDate(), game.getStartTime()).plusHours(2); // ⏰ 실제: 경기 시작 2시간 후

        taskScheduler.schedule(() -> {
            relayAsyncExecutor.stopRelay(gameId);
            log.info("[중계 종료 예약 실행] gameId: {}", gameId);
        }, stopTime.atZone(ZoneId.systemDefault()).toInstant());

        log.info("[중계 종료 예약 완료] gameId: {}, 종료시각: {}", gameId, stopTime);
    }



    private String generateGameId(Games game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }
}
