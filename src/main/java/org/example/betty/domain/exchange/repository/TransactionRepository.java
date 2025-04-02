package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.enums.TransactionStatus;
import org.example.betty.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);

    // 특정 지갑의 가장 최근 PENDING 상태 트랜잭션 1건 조회
    Optional<Transaction> findFirstByWallet_WalletAddressAndTransactionStatusOrderByCreatedAtDesc(
            String walletAddress,
            TransactionStatus transactionStatus
    );
}