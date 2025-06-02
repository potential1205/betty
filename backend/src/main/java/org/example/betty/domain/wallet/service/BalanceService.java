package org.example.betty.domain.wallet.service;


import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.entity.WalletBalance;

import java.util.List;

public interface BalanceService {
    void syncAllWalletBalances(); // 전체 유저 팬토큰 잔고 동기화
    void syncWalletBalance(Wallet wallet, String tokenName, String tokenAddress); // 특정 지갑, 토큰만 동기화

    List<WalletBalance> getAllByWallet(Wallet wallet);
}