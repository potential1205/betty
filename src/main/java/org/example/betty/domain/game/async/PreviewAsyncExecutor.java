package org.example.betty.domain.game.async;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.game.dto.redis.preview.TeamComparisonDto;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.external.game.scraper.GamePreviewScraper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewAsyncExecutor {

    private final GamePreviewScraper gamePreviewScraper;

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    @Async
    public void runAsync(String gameId, int seleniumIndex, String redisKey) {
        TeamComparisonDto preview = gamePreviewScraper.scrapePreview(gameId, seleniumIndex);
        if (preview != null) {
            HashOperations<String, String, Object> hashOps = redisTemplate2.opsForHash();
            hashOps.put(redisKey, "preview", preview);
            log.info("[프리뷰 저장 완료] - gameId: {}", gameId);
        } else {
            log.warn("[프리뷰 저장 실패] preview가 null - gameId: {}", gameId);
        }
    }
}
