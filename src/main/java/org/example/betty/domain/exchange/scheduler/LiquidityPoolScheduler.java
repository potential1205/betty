package org.example.betty.domain.exchange.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.service.HourlyPriceService;
import org.example.betty.domain.exchange.service.PriceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiquidityPoolScheduler {

    private final PriceService priceService;
    private final HourlyPriceService hourlyPriceService;

    // 1초마다 팬토큰 가격을 동기화
    @Scheduled(fixedRate = 1000)
    public void syncTokenPrices() {
        // log.info("[LP Scheduler] Price sync started.");
        priceService.syncAllPrices();
        // log.info("[LP Scheduler] Price sync completed.");
    }

    // 1시간마다 가격을 동기화 (syncHourlyPrices 메서드 호출)
    @Scheduled(cron = "0 0 * * * ?") // 매시 정각에 동작
    public void syncHourlyTokenPrices() {
//        log.info("[LP Scheduler] Hourly price sync started.");
        hourlyPriceService.syncHourlyPrices(); // HourlyPriceServiceImpl에서 동기화
//        log.info("[LP Scheduler] Hourly price sync completed.");
    }
}
