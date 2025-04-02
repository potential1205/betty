package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.repository.TransactionRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactions(String accessToken) {

        String walletAddress = sessionUtil.getSession(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        return transactionRepository.findByWallet(wallet);
    }
}
