package org.example.betty.domain.wallet.repository;

import org.example.betty.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletAddress(java.lang.String walletAddress);
    boolean existsByWalletAddress(String walletAddress);
}
