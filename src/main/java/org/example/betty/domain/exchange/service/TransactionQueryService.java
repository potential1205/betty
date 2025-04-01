package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.entity.Transaction;

import java.util.List;

public interface TransactionQueryService {
    List<Transaction> getTransactions(String accessToken);
}
