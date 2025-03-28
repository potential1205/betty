package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);
}