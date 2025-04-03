package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.service.GameCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class GameCacheRecoveryRunner implements CommandLineRunner {

    private final GameCacheService gameCacheService;

    @Override
    public void run(String... args) {
        LocalDate today = LocalDate.now();
        boolean recovered = gameCacheService.recoverTodayGameSchedule(today);

        if (recovered) {
            log.info("[복구] 오늘 예정 경기 캐싱 완료");
        } else {
            log.warn("[복구] 오늘 예정 경기 없음");
        }
    }
}