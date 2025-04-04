package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayAsyncExecutor {

    private final LiveRelayScraper liveRelayScraper;
    private final TaskScheduler taskScheduler;

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    /**
     * 실시간 중계 크롤링을 5초 간격으로 실행하는 메서드
     * @param gameId 경기 고유 ID
     * @param seleniumIndex 사용할 Selenium 컨테이너 인덱스
     */
    @Async
    public void startRelay(String gameId, int seleniumIndex) {
        Runnable task = () -> {
            try {
                RedisGameRelay relayData = liveRelayScraper.scrapeRelay(gameId, seleniumIndex);
                saveRelayDataToRedis(gameId, relayData);
                log.info("[중계 크롤링] gameId: {} - 크롤링 완료", gameId);
            } catch (Exception e) {
                log.error("[중계 크롤링 실패] gameId: {}", gameId, e);
            }
        };

        // 5초마다 반복 실행
        taskScheduler.scheduleAtFixedRate(task, new Date(), 5000L);
    }

    private void saveRelayDataToRedis(String gameId, RedisGameRelay relayData) {
        String redisKey = "games:" + LocalDate.now() + ":" + gameId;
        HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();
        hashOps.put(redisKey, "relay", relayData);

        log.info("[중계 저장] gameId: {} - Redis 저장 완료", gameId);
    }

    public void stopRelay(String gameId) {
        liveRelayScraper.stopRelay(gameId);
        log.info("[RelayAsyncExecutor] 중계 수동 종료 - gameId: {}", gameId);
    }

}
