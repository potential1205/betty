package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.game.async.LineupAsyncExecutor;
import org.example.betty.domain.game.async.RelayAsyncExecutor;
import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
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
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GameCacheServiceImpl implements GameCacheService {
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    private final LineupScraper lineupScraper;
    private final LineupAsyncExecutor lineupAsyncExecutor;
    private final RelayAsyncExecutor relayAsyncExecutor;
    private final GameService gameService;
    private final SseService sseService;
    private final GameRepository gameRepository;
    private final GameSettleService gameSettleService;
    private final TeamRepository teamRepository;
    private final TokenRepository tokenRepository;

    public static final String REDIS_GAME_PREFIX = "games:";

    /**
     * 당일 경기 정보 DB에서 조회에서 캐싱
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cacheDailyGames() {
        LocalDate today = LocalDate.now();
//        LocalDate today = LocalDate.now().minusDays(1);

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
                
                // 1. 당일 경기 일정 캐싱
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
                log.info("[캐싱 완료] 경기일정저장 - gameId: {}", gameId);
                
                // 2. 호출을 위한 정보 세팅
                Long id = gameService.resolveGameDbId(gameId);
                Map<String, Long> teamIds = gameService.resolveTeamIdsFromGameId(gameId);
                Long homeTeamId = teamIds.get("homeTeamId");
                Long awayTeamId = teamIds.get("awayTeamId");
                log.info("경기ID와 홈팀&원정팀ID : {} {} {}", id, homeTeamId, awayTeamId);

                // Team 객체 가져오기 (예외처리 없이 바로 호출)
                Team homeTeam = teamRepository.findById(homeTeamId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

                Team awayTeam = teamRepository.findById(awayTeamId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

                // Token 객체 가져오기 (예외처리 없이 바로 호출)
                Token homeToken = tokenRepository.findByTokenName(homeTeam.getTokenName())
                        .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

                Token awayToken = tokenRepository.findByTokenName(awayTeam.getTokenName())
                        .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

                // 3. 승리팀 베팅 시작
                gameSettleService.createGame(
                        id,
                        teamIds.get("homeTeamId"),
                        teamIds.get("awayTeamId"),
                        0L,
                        homeToken.getTokenAddress(),
                        awayToken.getTokenAddress()
                );

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
                scheduleRelayJob(game); // 중계 크롤링 예약
            }
            // Redis 키 만료 설정
            LocalDateTime expireTime = LocalDateTime.of(today, LocalTime.MAX);
            Date expireDate = Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant());
            redisTemplate2.expireAt(redisKey, expireDate);
        }
    }

    /**
     * 앱 재실행 시 경기 정보 캐싱
     */
    @Override
    @Transactional
    public boolean recoverTodayGameSchedule(LocalDate targetDate) {
        List<Game> todayGames = gameRepository.findByGameDate(targetDate);
        log.info("[복구] 오늘 경기 {}개 → 캐싱 및 스케줄링 재시도", todayGames.size());
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

        final Integer seleniumIndex;
        try {
            seleniumIndex = (Integer) redisTemplate2.opsForHash().get(redisKey, "seleniumIndex");
            if (seleniumIndex == null) {
                log.warn("[라인업 크롤링 취소] seleniumIndex 없음 - gameId: {}", gameId);
                return;
            }
        } catch (Exception e) {
            log.error("[라인업 예약 실패] Redis 접근 중 오류 - gameId: {}, error: {}", gameId, e.getMessage(), e);
            return;
        }

        Runnable task = () -> {
            try {
                RedisGameLineup lineup = lineupScraper.scrapeLineup(gameId, seleniumIndex);
                if (lineup != null) {
                    redisTemplate2.opsForHash().put(redisKey, "lineup", lineup);
                    log.info("[라인업 저장 완료] - gameId: {}, time: {}", gameId, LocalDateTime.now());
                } else {
                    log.warn("[라인업 저장 실패] 라인업이 null임 - gameId: {}, time: {}", gameId, LocalDateTime.now());
                }
            } catch (Exception e) {
                log.error("[라인업 크롤링 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
            }
        };

        if (executeTime.isBefore(LocalDateTime.now())) {
            lineupAsyncExecutor.runAsync(task);
            log.info("[라인업 즉시 실행] 경기 시작 30분 전 이미 지남 - gameId: {}", gameId);
        } else {
            taskScheduler.schedule(
                    () -> lineupAsyncExecutor.runAsync(task),
                    executeTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[라인업 크롤링 예약] - gameId: {}, 실행시각: {}", gameId, executeTime);
        }
    }

    /**
     * Relay 크롤링 예약: Lineup이 저장된 후에 실행
     */
    private void scheduleRelayJob(Game game) {
        String gameId = generateGameId(game);
        String redisKey = REDIS_GAME_PREFIX + game.getGameDate() + ":" + gameId;
        LocalDateTime gameStartTime = LocalDateTime.of(game.getGameDate(), game.getStartTime()).plusMinutes(1);

        final Integer seleniumIndex;
        try {
            seleniumIndex = (Integer) redisTemplate2.opsForHash().get(redisKey, "seleniumIndex");
            if (seleniumIndex == null) {
                log.warn("[중계 크롤링 취소] seleniumIndex 없음 - gameId: {}", gameId);
                return;
            }
        } catch (Exception e) {
            log.error("[중계 크롤링 취소] Redis 접근 실패 - gameId: {}, error: {}", gameId, e.getMessage(), e);
            return;
        }

        if (gameStartTime.isBefore(LocalDateTime.now())) {
            try {
                gameService.updateGameStatusToLive(game);
                sseService.send(gameId, "LIVE");
                relayAsyncExecutor.startRelay(gameId, seleniumIndex);
                log.info("[중계 크롤링 시작] 경기 시작 시간 지남 - gameId: {}", gameId);
            } catch (Exception e) {
                log.error("[중계 크롤링 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
            }
        } else {
            taskScheduler.schedule(
                    () -> {
                        try {
                            gameService.updateGameStatusToLive(game);
                            sseService.send(gameId, "LIVE");
                            relayAsyncExecutor.startRelay(gameId, seleniumIndex);
                            log.info("[중계 크롤링 시작] 예약 실행 - gameId: {}", gameId);

                            // 1. 승리팀 베팅 정산 호출
                            Long id = gameService.resolveGameDbId(gameId);
                            String key = "results:" + gameId;
                            PreVoteAnswer result = (PreVoteAnswer) redisTemplate2.opsForValue().get(key);

                            if (result == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_GAME_RESULT);
                            }
                            String winnerPrefix = result.getWinnerTeam();
                            Team winnerTeam = teamRepository.findByTeamNameStartingWith(winnerPrefix)
                                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));
                            Long winnerTeamId = winnerTeam.getId();
                            gameSettleService.preVoteSettle(id, winnerTeamId);
                            log.info("[승리팀 베팅 정산 호출 완료] gameId: {}", gameId);
                            
                            // 2. MVP 베팅 정산 호출
                            
                        } catch (Exception e) {
                            log.error("[중계 크롤링 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
                        }
                    },
                    gameStartTime.atZone(ZoneId.systemDefault()).toInstant()
            );
            log.info("[중계 크롤링 예약] 경기 시작 전 - gameId: {}, 시각: {}", gameId, gameStartTime);
        }
    }



    private String generateGameId(Game game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }

}
