package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.service.GameCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3) // Teams: 1, Games: 2 이후!
@RequiredArgsConstructor
public class RedisCacheInitRunner implements CommandLineRunner {

    private final GameCacheService gameCacheService;

    @Override
    public void run(String... args) {
        gameCacheService.cacheDailyGames();
        log.info("Redis 캐싱 테스트 실행 완료");
    }
}
