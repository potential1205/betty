package org.example.betty.domain.game.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.display.service.DisplayService;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.domain.game.service.GameRelayEventHandler;
import org.example.betty.domain.game.service.GameService;
import org.example.betty.domain.game.service.SseService;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayAsyncExecutor {

    private final LiveRelayScraper liveRelayScraper;
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> relayTasks = new ConcurrentHashMap<>();
    private final GameRelayEventHandler gameRelayEventHandler;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final GameService gameService;
    private final SseService sseService;
    private final GameResultAsyncExecutor gameResultAsyncExecutor;
    private final DisplayService displayService;


    //실시간 중계 크롤링을 5초 간격으로 실행하는 메서드
    @Async
    public void startRelay(String gameId, int seleniumIndex) {
        liveRelayScraper.initDriver(gameId, seleniumIndex);

        Runnable task = () -> {
            try {
                RedisGameRelay relayData = liveRelayScraper.scrapeRelay(gameId);

                if (relayData == null) {
                    stopRelay(gameId);
                    log.info("[중계 중단] gameId: {} - 경기 종료 감지로 반복 크롤링 중단", gameId);

                    // 전광판 이미지 저장
                    long id = gameService.resolveGameDbId(gameId);
                    Map<String, Long> teamIds = gameService.resolveTeamIdsFromGameId(gameId);
                    displayService.gameEnd(id, teamIds.get("awayTeamId"));
                    displayService.gameEnd(id, teamIds.get("homeTeamId"));

                    // 경기 종료 상태 업데이트
                    Game game = gameService.findGameByGameId(gameId);
                    sseService.send(gameId, "ENDED");
                    gameService.updateGameStatusToEnded(game);

                    // 결과 스크래핑 시작
                    gameResultAsyncExecutor.executeResultScraping(gameId, seleniumIndex);
                    log.info("[결과 크롤링 시작] gameId: {}, seleniumIndex: {}", gameId, seleniumIndex);
                }

                saveRelayDataToRedis(gameId, relayData);
                log.info("[중계 크롤링] gameId: {} - 크롤링 완료", gameId);
                gameRelayEventHandler.handleRelayUpdate(gameId, relayData);
                gameRelayEventHandler.handleGameInfoChange(gameId, relayData);

            } catch (Exception e) {
                log.error("[중계 크롤링 실패] gameId: {}", gameId, e);
            }
        };

        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, new Date(), 5000L);
        relayTasks.put(gameId, future);
    }

    public void stopRelay(String gameId) {
        ScheduledFuture<?> future = relayTasks.get(gameId);
        if (future != null) {
            future.cancel(false);
            relayTasks.remove(gameId);
            log.info("[중계 중단] gameId: {} - 5초 반복 크롤링 종료", gameId);
        }
    }

    private void saveRelayDataToRedis(String gameId, RedisGameRelay relayData) {
        String redisKey = "games:" + LocalDate.now() + ":" + gameId;
        HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();
        hashOps.put(redisKey, "relay", relayData);

        log.info("[중계 저장] gameId: {} - Redis 저장 완료", gameId);
    }
}