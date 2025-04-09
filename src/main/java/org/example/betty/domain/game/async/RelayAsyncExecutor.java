package org.example.betty.domain.game.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.display.service.DisplayService;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.service.*;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayAsyncExecutor {
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    private final LiveRelayScraper liveRelayScraper;
    private final Map<String, ScheduledFuture<?>> relayTasks = new ConcurrentHashMap<>();
    private final GameRelayEventHandler gameRelayEventHandler;
    private final GameService gameService;
    private final SseService sseService;
    private final GameResultAsyncExecutor gameResultAsyncExecutor;
    private final DisplayService displayService;
    private final GameSettleService gameSettleService;


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
                    
                    // 경기 종료 시 호출해야하는 작업을 위한 정보 세팅
                    long id = gameService.resolveGameDbId(gameId);
                    Map<String, Long> teamIds = gameService.resolveTeamIdsFromGameId(gameId);
                    log.info("경기ID와 홈팀&원정팀ID : {} {} {}", id, teamIds.get("homeTeamId"), teamIds.get("awayTeamId"));

                    // 1. 전광판 종료처리 (홈팀/원정팀)
                    displayService.gameEnd(id, teamIds.get("homeTeamId"));
                    displayService.gameEnd(id, teamIds.get("awayTeamId"));
                    log.info("[전광판 종료 완료] gameId: {}", gameId);

                    // 2. 경기 상태 종료 처리 + SSE 알림
                    Game game = gameService.findGameByGameId(gameId);
                    sseService.send(gameId, "ENDED");
                    gameService.updateGameStatusToEnded(game);
                    log.info("[경기 상태 종료 완료] gameId: {}", gameId);

                    // 3. 라이브 투표 정산 호출
                    gameSettleService.liveVoteSettle(id);
                    log.info("[LIVE 투표 정산 호출 완료] gameId: {}", gameId);

                    // 4. 결과(승리팀&mvp) 스크래핑 시작
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