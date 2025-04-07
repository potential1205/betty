package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.service.GameCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@Order(3)  // 3순위로 실행 (수정)
@RequiredArgsConstructor
public class GameCacheRecoveryRunner implements CommandLineRunner {

    private final GameCacheService gameCacheService;
    private final GameRepository gamesRepository;

    @Override
    public void run(String... args) {
        // Games 테이블에 데이터가 있을 때만 복구 작업을 진행
        if (gamesRepository.count() > 0) {
            LocalDate today = LocalDate.now();
//            LocalDate today = LocalDate.now().minusDays(1);
            boolean recovered = gameCacheService.recoverTodayGameSchedule(today);

            if (recovered) {
                log.info("[복구] 오늘 예정 경기 캐싱 완료");
            } else {
                log.warn("[복구] 오늘 예정 경기 없음");
            }
        } else {
            log.warn("[복구] Games 테이블에 데이터가 없습니다. 복구 작업을 실행할 수 없습니다.");
        }
    }
}
