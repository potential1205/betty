package org.example.betty.domain.wallet.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.wallet.service.BalanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletBalanceScheduler {

    private final BalanceService balanceService;

    // 매 1초마다 모든 지갑의 잔고를 동기화한다
    @Scheduled(fixedRate = 1000)
    public void runBalanceSync() {
        log.info("[Wallet Scheduler] Balance Sync started.");
        balanceService.syncAllWalletBalances();
        log.info("[Wallet Scheduler] Balance Sync completed.");
    }
}
