package org.example.betty.domain.exchange.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.enums.TransactionStatus;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.exchange.repository.TransactionRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TokenRepository tokenRepository;
    private final Web3jService web3jService;

    @Override
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        // 트랜잭션 객체 생성 후 PENDING 상태로 저장
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenTo(token)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        TransactionResponse pendingResponse = new TransactionResponse(
                true, "트랜잭션 처리 중입니다. 잠시만 기다려 주세요.", transaction.getId()
        );

        // 비동기로 블록체인 전송 처리
        new Thread(() -> handleBlockchainTransaction(transaction)).start();

        return pendingResponse;
    }

    private void handleBlockchainTransaction(Transaction transaction) {
        try {
            String txHash = web3jService.sendTransaction("contractAddress", BigInteger.ZERO, "encodedFunction").getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TransactionResponse processSwap(SwapRequest request, String accessToken) {
        return null;
    }
}
