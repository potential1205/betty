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
import java.util.Map;

@Slf4j
@Service
public class GameCacheServiceImpl implements GameCacheService {

    private final GamesRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final LiveRelayScraper liveRelayScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LineupAsyncExecutor lineupAsyncExecutor;
    private final RelayAsyncExecutor relayAsyncExecutor;


    private static final DateTimeFormatter GAME_ID_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String REDIS_GAME_PREFIX = "games:";

    public GameCacheServiceImpl(GamesRepository gameRepository,
                                LineupScraper lineupScraper,
                                LiveRelayScraper liveRelayScraper,
                                TaskScheduler taskScheduler,
                                RedisTemplate<String, Object> redisTemplate,
                                LineupAsyncExecutor lineupAsyncExecutor,
                                RelayAsyncExecutor relayAsyncExecutor) {
        this.gameRepository = gameRepository;
        this.lineupScraper = lineupScraper;
        this.liveRelayScraper = liveRelayScraper;
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

            LocalDateTime expireTime = LocalDateTime.of(today, LocalTime.MAX);
            Date expireDate = Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant());
            redisTemplate.expireAt(redisKey, expireDate);


            if(!"CANCELED".equalsIgnoreCase(game.getStatus())) {
                scheduleLineupJob(game);
                scheduleRelayJob(game);
            }
        }

    }

    @Override
    @Transactional
    public boolean recoverTodayGameSchedule(LocalDate targetDate) {
        List<Games> todayGames = gameRepository.findByGameDate(targetDate);
        if (todayGames.isEmpty()) return false;

        for (Games game : todayGames) {
            String gameId = generateGameId(game);
            String redisKey = REDIS_GAME_PREFIX + targetDate + ":" + gameId;

            boolean gameInfoExists = redisTemplate.hasKey(redisKey);
            if (!gameInfoExists) {
                log.warn("[복구] Redis에 경기일정 없음 → 캐싱 실행");
                cacheDailyGames();
                return true;
            }
        }
        return true;
    }


    /**
     * 라인업 스케줄링: 경기 시작 30분 전에 실행 or 이미 지났으면 즉시 실행
     */
    private void scheduleLineupJob(Games game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
        LocalDateTime gameStartDateTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());
        LocalDateTime executeTime = gameStartDateTime.minusMinutes(30);

        // 테스트용
//        LocalDateTime executeTime = LocalDateTime.now().plusSeconds(10);

        Runnable task = () -> {
            RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId);
            if (lineup != null) {
                redisTemplate.opsForHash().put(redisKey, "lineup", lineup);
                log.info("[라인업 저장 완료] - gameId: {}", gameId);
            } else {
                log.warn("[라인업 저장 실패] - gameId: {}", gameId);
            }
        };
        
        // 예약 시간 지나면 즉시 실행
        if (executeTime.isBefore(LocalDateTime.now())) {
            lineupAsyncExecutor.runAsync(task);
            log.info("[복구-즉시 실행] 라인업 크롤링 - gameId: {}", gameId);
        } else { // 아니면 예약 실행
            taskScheduler.schedule(
                    () -> lineupAsyncExecutor.runAsync(task),
                    executeTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[라인업 크롤링 예약] gameId: {}, 실행 예정 시각: {}", gameId, executeTime);
        }
    }



    /**
     * 실시간 경기 중계 스케줄링: 경기 시작 시간에 예약
     */
    private void scheduleRelayJob(Games game) {
        String gameId = generateGameId(game);
        LocalDateTime gameStartDateTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());

        // 예약 시간이 지나면 즉시 실행
        if (gameStartDateTime.isBefore(LocalDateTime.now())) {
            relayAsyncExecutor.startRelay(gameId);
            log.info("[즉시 실행] 중계 크롤링 실행됨 - gameId: {}", gameId);
        } else {
            // 아니면 예약 실행
            taskScheduler.schedule(
                    () -> relayAsyncExecutor.startRelay(gameId),
                    gameStartDateTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[예약 완료] 중계 크롤링 예약됨 - gameId: {}, 실행 시각: {}", gameId, gameStartDateTime);
        }
    }


    private String generateGameId(Games game) {
        // 게임 ID는 'yyyyMMdd+홈팀코드+원정팀코드+시즌' 형식으로 생성
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getHomeTeam().getTeamCode()
                + game.getAwayTeam().getTeamCode()
                + "0" + game.getSeason();  // 시즌을 추가
    }


}
