package org.example.betty.domain.game.service;

import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.GameBasicInfo;
import org.example.betty.domain.game.dto.redis.RedisGameData;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.external.game.scraper.LineupScraper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter GAME_ID_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String REDIS_GAME_PREFIX = "games:";

    public GameServiceImpl(GameRepository gameRepository, LineupScraper lineupScraper, TaskScheduler taskScheduler, RedisTemplate<String, Object> redisTemplate) {
        this.gameRepository = gameRepository;
        this.lineupScraper = lineupScraper;
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        selectDailyGames();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public List<GameInfoResponse> selectDailyGames() {
        LocalDate today = LocalDate.now();
        String redisKey = REDIS_GAME_PREFIX + today;

        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> existingGameDataMap = hashOps.entries(redisKey);

        List<Games> todayGames = gameRepository.findByGameDate(today);

        if (!existingGameDataMap.isEmpty()) {
            log.info("Redis에 오늘 날짜 경기 정보 이미 존재 - {}", redisKey);

            for (Games game : todayGames) {
                scheduleLineupJob(game);
            }

            return existingGameDataMap.values().stream()
                    .filter(value -> value instanceof RedisGameData)
                    .map(value -> {
                        RedisGameData data = (RedisGameData) value;
                        GameBasicInfo info = data.getGameInfo();
                        return GameInfoResponse.builder()
                                .season(info.getSeason())
                                .gameDate(LocalDate.parse(info.getGameDate()))
                                .startTime(LocalDateTime.parse(info.getStartTime()).toLocalTime())
                                .stadium(info.getStadium())
                                .homeTeamName(info.getHomeTeam())
                                .awayTeamName(info.getAwayTeam())
                                .status(info.getStatus())
                                .build();
                    }).toList();
        }

        for (Games game : todayGames) {
            String gameId = generateGameId(game);
            GameBasicInfo gameInfo = GameBasicInfo.builder()
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeam(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeam(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();

            RedisGameData redisGameData = RedisGameData.builder()
                    .gameInfo(gameInfo)
                    .lineup(null)
                    .relay(null)
                    .build();

            hashOps.put(redisKey, gameId, redisGameData);
            scheduleLineupJob(game);
        }

        List<GameInfoResponse> responseList = new ArrayList<>();
        for (Games game : todayGames) {
            GameInfoResponse response = GameInfoResponse.builder()
                    .season(game.getSeason())
                    .gameDate(game.getGameDate())
                    .startTime(game.getStartTime())
                    .stadium(game.getStadium())
                    .homeTeamName(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeamName(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();
            responseList.add(response);
        }

        log.info("오늘경기 캐싱 및 응답: {}", responseList);
        return responseList;
    }

    private void scheduleLineupJob(Games game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate();

        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        RedisGameData redisGameData = (RedisGameData) hashOps.get(redisKey, gameId);

        if (redisGameData != null && redisGameData.getLineup() != null) {
            log.info("redis에 이미 존재하는 라인업 - gameId: {}", gameId);
            return;
        }

        LocalDateTime executeTime = LocalDateTime.of(game.getGameDate(), game.getStartTime().minusMinutes(30));

        if (executeTime.isBefore(LocalDateTime.now())) {
            log.info("[즉시 실행] 라인업 크롤링 - gameId: {}", gameId);
            lineupScraper.scrapeLineup(gameId);
        } else {
            taskScheduler.schedule(() -> {
                log.info("[자동 실행] 라인업 크롤링 - gameId: {}", gameId);
                lineupScraper.scrapeLineup(gameId);
            }, executeTime.atZone(ZoneId.systemDefault()).toInstant());

            log.info("라인업 예약 완료 - gameId: {}, 실행시각: {}", gameId, executeTime);
        }
    }

    private String generateGameId(Games game) {
        return game.getGameDate().format(GAME_ID_DATE_FORMAT)
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + 0 + game.getSeason();
    }

    @Override
    public void crawlLiveGameInfo() {

    }
}
