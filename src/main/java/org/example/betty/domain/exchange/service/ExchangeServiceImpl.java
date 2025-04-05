package org.example.betty.domain.exchange.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.PendingTransactionUtil;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.contract.Exchange;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

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
    private final PendingTransactionUtil pendingTransactionUtil;

    @Value("${exchange.address}")
    private String exchangeAddress;

    // 1-1. add 요청 처리
    public TransactionResponse processAdd(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleAddTransaction(transaction)).start();

        return new TransactionResponse(true, "충전 요청이 처리 중입니다.", transaction.getId());
    }

    // 1-2. add 블록체인 트랜잭션 처리
    private void handleAddTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            TransactionReceipt receipt = contract.add(transaction.getAmountIn().toBigInteger()).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }

    // 2-1. remove 요청 처리
    public TransactionResponse processRemove(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleRemoveTransaction(transaction)).start();

        return new TransactionResponse(true, "출금 요청이 처리 중입니다.", transaction.getId());
    }

    // 2-2. remove 블록체인 트랜잭션 처리
    private void handleRemoveTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            TransactionReceipt receipt = contract.remove(transaction.getAmountIn().toBigInteger()).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }

    // 3-1. buy 요청 처리
    @Override
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenTo(token)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleBuyTransaction(transaction)).start();

        return new TransactionResponse(true, "트랜잭션 처리 중입니다.", transaction.getId());
    }

    // 3-2. buy 블록체인 트랜잭션 처리
    private void handleBuyTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            String tokenName = transaction.getTokenTo().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.buy(tokenName, amount).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }

    // 4-1. sell 요청 처리
    public TransactionResponse processSell(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(token)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleSellTransaction(transaction)).start();

        return new TransactionResponse(true, "판매 요청이 처리 중입니다.", transaction.getId());
    }

    // 4-2. sell 블록체인 트랜잭션 처리
    private void handleSellTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            String tokenName = transaction.getTokenFrom().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.sell(tokenName, amount).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }

    // 5-1. swap 요청 처리
    @Override
    @Transactional
    public TransactionResponse processSwap(SwapRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Token tokenFrom = tokenRepository.findById(request.getTokenFromId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Token tokenTo = tokenRepository.findById(request.getTokenToId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(tokenFrom)
                .tokenTo(tokenTo)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleSwapTransaction(transaction)).start();

        return new TransactionResponse(true, "스왑 트랜잭션 처리 중입니다.", transaction.getId());
    }

    // 5-2. swap 블록체인 트랜잭션 처리
    private void handleSwapTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            String tokenFromName = transaction.getTokenFrom().getTokenName();
            String tokenToName = transaction.getTokenTo().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.swap(tokenFromName, tokenToName, amount).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }

    // 6-1. use 요청 처리
    @Override
    @Transactional
    public TransactionResponse processUse(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(token)
                .amountIn(request.getAmountIn())
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleUseTransaction(transaction)).start();

        return new TransactionResponse(true, "사용 요청이 처리 중입니다.", transaction.getId());
    }

    // 6-2. use 블록체인 트랜잭션 처리
    private void handleUseTransaction(Transaction transaction) {
        try {
            Exchange contract = Exchange.load(exchangeAddress, web3jService.getWeb3j(), web3jService.getCredentials(), new DefaultGasProvider());
            String tokenName = transaction.getTokenFrom().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.use(tokenName, amount).send();
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            e.printStackTrace();
        }
    }
}
