package org.example.betty.domain.exchange.service;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.PendingTransactionUtil;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.contract.Exchange;
import org.example.betty.contract.Token;
import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.example.betty.domain.exchange.entity.FanToken;
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
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TokenRepository tokenRepository;
    private final Web3jService web3jService;
    private final PendingTransactionUtil pendingTransactionUtil;

    @Value("${BET_ADDRESS}")
    private String betTokenAddress;

    @Value("${exchange.address}")
    private String exchangeAddress;

    // 1-1. add 요청 처리
    public TransactionResponse processAdd(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        FanToken bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(null) // KRW
                .fanTokenTo(bet)
                .amountIn(request.getAmountIn())
                .amountOut(null)
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
            BigDecimal amountKrw = transaction.getAmountIn(); // KRW
            BigDecimal amountBet = amountKrw.divide(BigDecimal.valueOf(100)); // 1BET = 100KRW
            BigInteger amountWei = amountBet.toBigInteger();

            // 운영 지갑 Credentials, GasProvider
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            // 컨트랙트 로드
            Token betToken = Token.load(
                    betTokenAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );
            Exchange exchangeContract = Exchange.load(
                    exchangeAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );
            // approve
            TransactionReceipt approveReceipt = betToken.approve(exchangeAddress, amountWei).send();
            log.info("[APPROVE SUCCESS] token={}, txHash={}", betTokenAddress, approveReceipt.getTransactionHash());

            // add
            TransactionReceipt addReceipt = exchangeContract.add(amountWei).send();
            log.info("[ADD SUCCESS] wallet={}, amount={}, txHash={}", credentials.getAddress(), amountBet, addReceipt.getTransactionHash());

            transaction.updateAmountOut(amountBet);
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("[ADD TRANSACTION FAILED] wallet={}, reason={}", web3jService.getCredentials().getAddress(), e.getMessage(), e);
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
        }
    }

    // 2-1. remove 요청 처리
    public TransactionResponse processRemove(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        FanToken bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(bet)
                .fanTokenTo(null) // KRW
                .amountIn(request.getAmountIn())
                .amountOut(null)
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            TransactionReceipt receipt = contract.remove(transaction.getAmountIn().toBigInteger()).send();
            // 1BET = 100KRW
            BigDecimal krw = transaction.getAmountIn().multiply(BigDecimal.valueOf(100)); // amountIn == BET
            transaction.updateAmountOut(krw);
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
        FanToken fanToken = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        FanToken bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(bet)
                .fanTokenTo(fanToken)
                .amountIn(request.getAmountIn())
                .amountOut(null)
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            String tokenName = transaction.getFanTokenTo().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.buy(tokenName, amount).send();
            // 이벤트에서 amountOut 파싱
            List<Exchange.BuyExecutedEventResponse> events = Exchange.getBuyExecutedEvents(receipt);
            if(!events.isEmpty()) {
                BigInteger amountOut = events.get(0).amountOut;
                transaction.updateAmountOut(new BigDecimal(amountOut));
            }
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
        FanToken fanToken = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        FanToken bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(fanToken)
                .fanTokenTo(bet)
                .amountIn(request.getAmountIn())
                .amountOut(null)
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            String tokenName = transaction.getFanTokenFrom().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.sell(tokenName, amount).send();
            // 이벤트에서 amountOut 파싱
            List<Exchange.SellExecutedEventResponse> events = Exchange.getSellExecutedEvents(receipt);
            if(!events.isEmpty()) {
                BigInteger amountOut = events.get(0).amountOut;
                transaction.updateAmountOut(new BigDecimal(amountOut));
            }
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

        FanToken fanTokenFrom = tokenRepository.findById(request.getTokenFromId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        FanToken fanTokenTo = tokenRepository.findById(request.getTokenToId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(fanTokenFrom)
                .fanTokenTo(fanTokenTo)
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            String tokenFromName = transaction.getFanTokenFrom().getTokenName();
            String tokenToName = transaction.getFanTokenTo().getTokenName();
            BigInteger amount = transaction.getAmountIn().toBigInteger();
            TransactionReceipt receipt = contract.swap(tokenFromName, tokenToName, amount).send();
            List<Exchange.SwapExecutedEventResponse> events = Exchange.getSwapExecutedEvents(receipt);
            if (!events.isEmpty()) {
                BigInteger amountOut = events.get(0).amountOut;
                transaction.updateAmountOut(new BigDecimal(amountOut));
            }
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
        FanToken fanToken = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .fanTokenFrom(fanToken)
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            String tokenName = transaction.getFanTokenFrom().getTokenName();
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
