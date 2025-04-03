package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.LiveRelayScraper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayAsyncExecutor {

    private final LiveRelayScraper liveRelayScraper;
    private final TaskScheduler taskScheduler;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 실시간 중계 크롤링을 5초 간격으로 실행하는 메서드
     * @param gameId 경기 고유 ID
     */
    @Async
    public void startRelay(String gameId) {
        Runnable task = () -> {
            try {
                // 크롤링 작업 실행
                Map<String, String> liveData = (Map<String, String>) liveRelayScraper.scrapeRelay(gameId);

                // 크롤링한 데이터를 Redis에 저장
                saveRelayDataToRedis(gameId, liveData);

                // 로그만 기록 (데이터 저장은 외부 메서드에서 처리)
                log.info("[중계 크롤링] gameId: {} - 크롤링 완료", gameId);
            } catch (Exception e) {
                log.error("[중계 크롤링 실패] gameId: {}", gameId, e);
            }
        };

        // 5초마다 반복 실행
        taskScheduler.scheduleAtFixedRate(task, new Date(), 5000L);   // 간격 5초
    }

    /**
     * 실시간 중계 데이터를 Redis에 저장하는 메서드
     * @param gameId 경기 고유 ID
     * @param liveData 실시간 중계 데이터
     */
    private void saveRelayDataToRedis(String gameId, Map<String, String> liveData) {
        // Redis 키 설정: 게임 ID에 해당하는 중계 정보 키
        String redisKey = "games:relay:" + gameId;

        // Redis HashOperations 사용하여 데이터를 저장
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        // "relay"라는 필드명으로 실시간 중계 데이터를 저장
        hashOps.put(redisKey, "relay", liveData);

        // 로그로 저장 완료 확인
        log.info("[중계 크롤링] gameId: {} - 중계 데이터 Redis에 저장됨", gameId);
    }
}
