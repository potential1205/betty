package org.example.betty.domain.game.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.service.GameProblemServiceImpl;
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

    private final LiveRelayScraper liveRelayScraper;
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> relayTasks = new ConcurrentHashMap<>();
    private final GameProblemServiceImpl gameProblemService;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

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
                    return;
                }

                saveRelayDataToRedis(gameId, relayData);
                log.info("[중계 크롤링] gameId: {} - 크롤링 완료", gameId);
                gameProblemService.handleRelayUpdate(gameId, relayData);

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
