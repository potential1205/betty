package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.async.LineupAsyncExecutor;
import org.example.betty.domain.game.async.RelayAsyncExecutor;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GamesRepository;
import org.example.betty.external.game.scraper.LineupScraper;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequiredArgsConstructor
public class GameCacheServiceImpl implements GameCacheService {

    private final GamesRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final TaskScheduler taskScheduler;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final LineupAsyncExecutor lineupAsyncExecutor;
    private final RelayAsyncExecutor relayAsyncExecutor;

    public static final String REDIS_GAME_PREFIX = "games:";

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cacheDailyGames() {
        LocalDate today = LocalDate.now();
        List<Game> todayGames = gameRepository.findByGameDate(today);
        HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();

        int index = 0;

        for (Game game : todayGames) {
            String gameId = generateGameId(game);
            String redisKey = REDIS_GAME_PREFIX + today + ":" + gameId;

            boolean isActive = !"CANCELED".equalsIgnoreCase(game.getStatus())
                    && !"ENDED".equalsIgnoreCase(game.getStatus());

            boolean isNewEntry = !hashOps.hasKey(redisKey, "gameInfo");

            if (isNewEntry) {
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
                log.info("[캐싱 완료] gameInfo 저장 - gameId: {}", gameId);
            } else {
                log.info("[캐싱 스킵] Redis에 이미 존재하는 경기 - gameId: {}", gameId);
            }

            // 모든 경기 seleniumIndex 캐싱
            hashOps.put(redisKey, "seleniumIndex", index % 5);
            index++;

            if (isActive) {
                if (hashOps.get(redisKey, "lineup") == null) {
                    scheduleLineupJob(game);   // 라인업 크롤링 예약
                } else {
                    log.info("[라인업 예약 스킵] 이미 캐싱됨 - gameId: {}", gameId);
                }

                scheduleRelayJob(game);    // 중계는 무조건 예약 또는 즉시 실행
            }

            // Redis 키 만료 설정
            LocalDateTime expireTime = LocalDateTime.of(today, LocalTime.MAX);
            Date expireDate = Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant());
            redisTemplate2.expireAt(redisKey, expireDate);
        }
    }


    @Override
    @Transactional
    public boolean recoverTodayGameSchedule(LocalDate targetDate) {
        List<Game> todayGames = gameRepository.findByGameDate(targetDate);
        log.info("[복구] 오늘 경기 {}개 → 캐시 및 스케줄링 재시도", todayGames.size());
        cacheDailyGames();
        return true;
    }

    /**
     * Lineup 크롤링 예약: 경기 시작 30분 전 또는 이미 지났으면 즉시 실행
     */
    private void scheduleLineupJob(Game game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
        LocalDateTime gameStartDateTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());
        LocalDateTime executeTime = gameStartDateTime.minusMinutes(30);

        // 테스트용: 지금부터 10초 뒤에 실행하고 싶을 때
//        LocalDateTime executeTime = LocalDateTime.now().plusSeconds(10);

        Integer seleniumIndex = (Integer) redisTemplate2.opsForHash().get(redisKey, "seleniumIndex");
        if (seleniumIndex == null) {
            log.warn("[라인업 크롤링 취소] seleniumIndex 없음 - gameId: {}", gameId);
            return;
        }

        Runnable task = () -> {
//            RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId);
            RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId, seleniumIndex);
            if (lineup != null) {
                redisTemplate2.opsForHash().put(redisKey, "lineup", lineup);
                log.info("[라인업 저장 완료] - gameId: {}, time: {}", gameId, LocalDateTime.now());
            } else {
                log.warn("[라인업 저장 실패] - gameId: {}, time: {}", gameId, LocalDateTime.now());
            }
        };

        if (executeTime.isBefore(LocalDateTime.now())) {
            lineupAsyncExecutor.runAsync(task);
        } else {
            taskScheduler.schedule(
                    () -> lineupAsyncExecutor.runAsync(task),
                    executeTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[라인업 크롤링 예약] 경기 시작 30분 전 - gameId: {}, 실행시각: {}", gameId, executeTime);
        }
    }



    /**
     * Relay 크롤링 예약: Lineup이 저장된 후에 실행
     */
    private void scheduleRelayJob(Game game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
        LocalDateTime gameStartTime = LocalDateTime.of(game.getGameDate(), game.getStartTime());

        // 테스트용
//        LocalDateTime gameStartTime = LocalDateTime.now().plusMinutes(2);


        final Integer seleniumIndex = (Integer) redisTemplate2.opsForHash().get(redisKey, "seleniumIndex");
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
//        scheduleRelayStopJob(game);
    }

    private String generateGameId(Game game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }



    // 타자 교체 감지

}
