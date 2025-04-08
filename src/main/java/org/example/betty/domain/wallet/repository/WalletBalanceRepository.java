package org.example.betty.domain.wallet.repository;

import org.example.betty.domain.exchange.entity.FanToken;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {
    Optional<WalletBalance> findByWalletAndToken(Wallet wallet, FanToken fanToken);

    Optional<WalletBalance> findByWalletIdAndTokenId(Long id, Long id1);
}