package org.example.betty.domain.game.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
import org.example.betty.domain.game.service.GameService;
import org.example.betty.external.game.scraper.GameResultScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameResultAsyncExecutor {

    private final GameResultScraper gameResultScraper;

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final ObjectMapper objectMapper;
    private final GameService gameService;

    @Async
    public void executeResultScraping(String gameId, int seleniumIndex) {
        try {
            gameResultScraper.initDriver(gameId, seleniumIndex);
            PreVoteAnswer result = gameResultScraper.scrapeResult(gameId);
            if (result != null) {
                saveToRedis(gameId, result);
                log.info("[게임 결과 저장 완료] gameId: {}, result: {}", gameId, result);
            } else {
                log.warn("[게임 결과 없음] gameId: {}", gameId);
            }
        } catch (Exception e) {
            log.error("[게임 결과 크롤링 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
        }
    }

    private void saveToRedis(String gameId, PreVoteAnswer result) {
        try {
            Long id = gameService.resolveGameDbId(gameId);
            String key = "results:" + id;
            String value = objectMapper.writeValueAsString(result);
            redisTemplate2.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("[Redis 저장 실패] gameId: {}, error: {}", gameId, e.getMessage(), e);
        }
    }
}
