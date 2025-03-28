package org.example.betty.domain.game.service;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.redis.RedisGameData;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.repository.GamesRepository;
import org.example.betty.external.game.scraper.LineupScraper;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class GameCacheServiceImpl implements GameCacheService {

    private final GamesRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final LiveRelayScraper liveGameScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter GAME_ID_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String REDIS_GAME_PREFIX = "games:";

    public GameCacheServiceImpl(GamesRepository gameRepository,
                                LineupScraper lineupScraper,
                                LiveRelayScraper liveGameScraper,
                                TaskScheduler taskScheduler,
                                @Qualifier("redisTemplate2") RedisTemplate<String, Object> redisTemplate) {
        this.gameRepository = gameRepository;
        this.lineupScraper = lineupScraper;
        this.liveGameScraper = liveGameScraper;
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void cacheDailyGames() {
        LocalDate today = LocalDate.now();
        String redisKey = REDIS_GAME_PREFIX + today;
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        List<Games> todayGames = gameRepository.findByGameDate(today);

        for (Games game : todayGames) {
            String gameId = generateGameId(game);

            RedisGameSchedule gameSchedule = RedisGameSchedule.builder()
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeam(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeam(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();

            RedisGameData redisGameData = RedisGameData.builder()
                    .gameSchedule(gameSchedule)
                    .lineup(null)
                    .relay(null)
                    .build();

            hashOps.put(redisKey, gameId, redisGameData);

//            scheduleLineupJob(game);
//            scheduleRelayJob(game);
        }

    }

    /**
     * 라인업 스케줄링: 경기 시작 30분 전에 실행 or 이미 지났으면 즉시 실행
     */
    private void scheduleLineupJob(Games game) {
        String gameId = generateGameId(game);
        LocalDateTime executeTime = LocalDateTime.of(game.getGameDate(), game.getStartTime().minusMinutes(30));

        if (executeTime.isBefore(LocalDateTime.now())) {
            lineupScraper.scrapeLineup(gameId);
            log.info("[즉시 실행] 라인업 크롤링 실행됨 - gameId: {}", gameId);
        } else {
            taskScheduler.schedule(() -> {
                lineupScraper.scrapeLineup(gameId);
                log.info("[예약 실행] 라인업 크롤링 완료 - gameId: {}", gameId);
            }, executeTime.atZone(ZoneId.systemDefault()).toInstant());

            log.info("라인업 예약 완료 - gameId: {}, 시간: {}", gameId, executeTime);
        }
    }

    /**
     * 실시간 경기 중계 스케줄링: 경기 시작 시간에 예약
     * (구현 내용은 복잡도 때문에 추후 추가)
     */
    private void scheduleRelayJob(Games game) {
        String gameId = generateGameId(game);

        log.info("[예약 예정] 중계 크롤링 예약됨 - gameId: {}", gameId);
    }

    private String generateGameId(Games game) {
        return game.getGameDate().format(GAME_ID_DATE_FORMAT)
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }
}
