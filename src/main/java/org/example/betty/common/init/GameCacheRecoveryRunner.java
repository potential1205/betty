package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.service.GameCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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
            log.info("[복구] 오늘 경기 캐싱 완료");
        } else {
            log.warn("[복구] 오늘 경기 없음 또는 복구 대상 없음");
        }
    }
}