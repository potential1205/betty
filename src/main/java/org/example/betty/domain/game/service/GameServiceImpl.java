package org.example.betty.domain.game.service;


import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.external.game.scraper.LineupScraper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final LineupScraper lineupScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 날짜 기반 경기ID 포맷 생성용
    private static final DateTimeFormatter GAME_ID_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Redis 저장 키 접두사
    private static final String REDIS_GAME_PREFIX = "games:";
    private static final String REDIS_LINEUP_PREFIX = "lineup:";

    public GameServiceImpl(GameRepository gameRepository, LineupScraper lineupScraper, TaskScheduler taskScheduler, RedisTemplate redisTemplate) {
        this.gameRepository = gameRepository;
        this.lineupScraper = lineupScraper;
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 앱 재실행 시 -> 당일 날짜 경기 목록 조회 강제 실행
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        selectDailyGames();
    }

    /**
     * 자정마다 실행: 경기 조회 + 라인업 예약
     */
    @Override
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * ?")
    public List<GameInfoResponse> selectDailyGames() {
        String gameKey = REDIS_GAME_PREFIX + LocalDate.now();

//        if(redis)

        List<Games> todayGames = gameRepository.findByGameDate(LocalDate.now());
        
        // 각 경기 30분 전 라인업 스크래핑 작업 예약 등록
        for (Games game : todayGames) {
            scheduleLineupJob(game);
        }
        
        List<GameInfoResponse> responseList =  todayGames.stream()
                .map(game -> GameInfoResponse.builder()
                        .season(game.getSeason())
                        .gameDate(game.getGameDate())
                        .startTime(game.getStartTime())
                        .stadium(game.getStadium())
                        .homeTeamName(game.getHomeTeam().getTeamName().split(" ")[0])
                        .awayTeamName(game.getAwayTeam().getTeamName().split(" ")[0])
                        .status(game.getStatus())
                        .build()
                ).toList();
        log.info("Today games: {}", responseList);
        return responseList;
    }

    /**
     * 각 경기 라인업 스케줄링 등록 (startTime - 30분)
     * TaskScheduler 사용해서 특정 시각에 scrapeLineup 실행 예약
     */
    private void scheduleLineupJob(Games game) {
        String gameId = generateGameId(game);
        LocalDateTime executeTime = LocalDateTime.of(game.getGameDate(), game.getStartTime().minusMinutes(30));

        taskScheduler.schedule(() -> {
            log.info("[자동 실행] 라인업 크롤링 - gameId: {}", gameId);
            lineupScraper.scrapeLineup(gameId);
        }, executeTime.atZone(ZoneId.systemDefault()).toInstant());

        log.info("라인업 예약 완료 - gameId: {}, 실행시각: {}", gameId, executeTime);
    }

    /**
     * 경기 아이디 생성 메서드
     * @param game
     * @return
     */
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