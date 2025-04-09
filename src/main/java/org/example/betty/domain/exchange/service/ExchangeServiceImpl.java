package org.example.betty.domain.exchange.service;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resolver.FanTokenAddressResolver;
import org.example.betty.common.util.PendingTransactionUtil;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.contract.Exchange;
import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.SwapEstimateResponse;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.enums.TransactionStatus;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.exchange.repository.TransactionRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.wallet.repository.WalletBalanceRepository;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.domain.wallet.service.BalanceService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
    private final BalanceService balanceService;
    private final TokenPriceRepository tokenPriceRepository;
    private final WalletBalanceRepository walletBalanceRepository;

    private final ContractGasProvider zeroGasProvider = new ContractGasProvider() {
        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return BigInteger.ZERO;
        }

        @Override
        public BigInteger getGasPrice() {
            return BigInteger.ZERO;
        }

        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return BigInteger.valueOf(5_000_000L);
        }

        @Override
        public BigInteger getGasLimit() {
            return BigInteger.valueOf(5_000_000L);
        }
    };

    @Value("${BET_ADDRESS}")
    private String betTokenAddress;

    @Value("${EXCHANGE_ADDRESS}")
    private String exchangeAddress;

    @Autowired
    private FanTokenAddressResolver fanTokenAddressResolver;

    // 1-1. add 요청 처리
    @Override
    public TransactionResponse processAdd(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Token bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(null) // KRW
                .tokenTo(bet)
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
            org.example.betty.contract.Token betToken = org.example.betty.contract.Token.load(
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

            BigInteger allowance = betToken.allowance(credentials.getAddress(), exchangeAddress).send();
            BigInteger MAX_UINT256 = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
            if (allowance.compareTo(amountWei) < 0) {
                log.info("[APPROVE] Allowance 부족 → 초기화 및 approve");
                betToken.approve(exchangeAddress, BigInteger.ZERO).send();
                betToken.approve(exchangeAddress, MAX_UINT256).send();
            }

            // ✅ addFrom(운영자, amount) 호출
            String userWalletAddress = transaction.getWallet().getWalletAddress();
            TransactionReceipt addFromReceipt = exchangeContract.addFrom(credentials.getAddress(), amountWei).send();
            log.info("[ADD_FROM SUCCESS] txHash={}", addFromReceipt.getTransactionHash());

            // ✅ 사용자 지갑으로 BET 전송
            TransactionReceipt transferReceipt = betToken.transfer(userWalletAddress, amountWei).send();
            log.info("[TRANSFER SUCCESS] toUser={}, amount={}, txHash={}", userWalletAddress, amountWei, transferReceipt.getTransactionHash());

            // 트랜잭션 상태 및 DB 업데이트
            transaction.updateAmountOut(amountBet);
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            // DB 잔고 동기화
            balanceService.syncWalletBalance(transaction.getWallet(), "BET", betTokenAddress);

        } catch (Exception e) {
            log.error("[ADD_FROM FAILED] 사용자={}, reason={}", transaction.getWallet().getWalletAddress(), e.getMessage(), e);
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
        }
    }

    // 2-1. remove 요청 처리
    @Override
    public TransactionResponse processRemove(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Token bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        WalletBalance walletBalance = walletBalanceRepository.findByWalletAndToken(wallet, bet)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET_BALANCE));

        BigDecimal currentBalance = walletBalance.getBalance();
        BigDecimal requestedAmount = request.getAmountIn();

        if (currentBalance.compareTo(requestedAmount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_TOKEN_AMOUNT);
        }

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(bet)
                .tokenTo(null) // KRW
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
            BigDecimal amountBet = transaction.getAmountIn(); // BET
            BigDecimal amountKrw = amountBet.multiply(BigDecimal.valueOf(100)); // 1 BET = 100 KRW
            BigInteger amountWei = amountBet.toBigInteger();

            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.Token betToken = org.example.betty.contract.Token.load(
                    betTokenAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );
            Exchange exchangeContract = Exchange.load(
                    exchangeAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            // 사용자 지갑에서 BET 출금 approve
            String userWalletAddress = transaction.getWallet().getWalletAddress();
            TransactionReceipt approveReceipt = betToken.approve(exchangeAddress, amountWei).send();
            log.info("[APPROVE SUCCESS] user={}, amount={}, txHash={}", userWalletAddress, amountBet, approveReceipt.getTransactionHash());

            // remove
            TransactionReceipt removeReceipt = exchangeContract.remove(amountWei).send();
            log.info("[REMOVE SUCCESS] wallet={}, amount={}, txHash={}", userWalletAddress, amountBet, removeReceipt.getTransactionHash());

            // 트랜잭션 업데이트
            transaction.updateAmountOut(amountKrw);
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            // 잔고 동기화
            BigInteger updatedWei = betToken.balanceOf(userWalletAddress).send();
            BigDecimal updatedBet = new BigDecimal(updatedWei);
            balanceService.syncWalletBalance(transaction.getWallet(), "BET", betTokenAddress);

        } catch (Exception e) {
            log.error("[REMOVE TRANSACTION FAILED] wallet={}, reason={}", transaction.getWallet().getWalletAddress(), e.getMessage(), e);
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
        }
    }

    // 3-1. buy 요청 처리
    @Override
    @Transactional
    public TransactionResponse processBuy(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        Token bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(bet)
                .tokenTo(token)
                .amountIn(request.getAmountIn())
                .amountOut(null)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        new Thread(() -> handleBuyTransaction(transaction)).start();

        return new TransactionResponse(true, "구매 요청 처리 중입니다.", transaction.getId());
    }

    // 3-2. buy 블록체인 트랜잭션 처리
    private void handleBuyTransaction(Transaction transaction) {
        try {
            BigDecimal amountBet = transaction.getAmountIn(); // BET
            BigInteger amountWei = amountBet.toBigInteger();

            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials(); // 운영 지갑
            long chainId = web3jService.getChainId();

            org.example.betty.contract.Token betToken = org.example.betty.contract.Token.load(
                    betTokenAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            Exchange exchangeContract = Exchange.load(
                    exchangeAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            // approve
            TransactionReceipt approveReceipt = betToken.approve(exchangeAddress, amountWei).send();
            log.info("[APPROVE SUCCESS] BET -> Exchange, txHash={}", approveReceipt.getTransactionHash());

            // buy
            String tokenName = transaction.getTokenTo().getTokenName();
            TransactionReceipt buyReceipt = exchangeContract.buy(tokenName, amountWei).send();
            log.info("[BUY SUCCESS] fanToken={}, txHash={}", tokenName, buyReceipt.getTransactionHash());

            // 이벤트에서 amountOut 파싱
            List<Exchange.BuyExecutedEventResponse> events = Exchange.getBuyExecutedEvents(buyReceipt);
            if (events.isEmpty()) {
                throw new RuntimeException("BuyExecuted 이벤트가 없습니다.");
            }

            BigInteger amountOut = events.get(0).amountOut;
            transaction.updateAmountOut(new BigDecimal(amountOut));

            // 팬토큰 컨트랙트
            String fanTokenAddress = fanTokenAddressResolver.getAddress(tokenName);
            if (fanTokenAddress == null) {
                throw new RuntimeException("팬토큰 주소를 찾을 수 없습니다: " + tokenName);
            }

            org.example.betty.contract.Token fanToken = org.example.betty.contract.Token.load(
                    fanTokenAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            // 사용자 지갑으로 전송
            String userWalletAddress = transaction.getWallet().getWalletAddress();
            TransactionReceipt transferReceipt = fanToken.transfer(userWalletAddress, amountOut).send();
            log.info("[TRANSFER SUCCESS] toUser={}, fanToken={}, txHash={}", userWalletAddress, tokenName, transferReceipt.getTransactionHash());

            // 트랜잭션 업데이트
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            // 잔고 동기화
            balanceService.syncWalletBalance(transaction.getWallet(), "BET", betTokenAddress);
            balanceService.syncWalletBalance(transaction.getWallet(), tokenName, fanTokenAddress);
        } catch (Exception e) {
            log.error("[BUY TRANSACTION FAILED] wallet={}, reason={}", transaction.getWallet().getWalletAddress(), e.getMessage(), e);
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
        }
    }

    // 4-1. sell 요청 처리
    @Override
    @Transactional
    public TransactionResponse processSell(TransactionRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        pendingTransactionUtil.throwIfPending(walletAddress);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        Token bet = tokenRepository.findByTokenName("BET")
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .tokenFrom(token)
                .tokenTo(bet)
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
            BigDecimal amountFan = transaction.getAmountIn(); // 팬토큰 수량
            BigInteger amountWei = amountFan.toBigInteger();

            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            Exchange exchangeContract = Exchange.load(
                    exchangeAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            String tokenName = transaction.getTokenFrom().getTokenName();
            String fanTokenAddress = fanTokenAddressResolver.getAddress(tokenName);
            if (fanTokenAddress == null) {
                throw new RuntimeException("팬토큰 주소를 찾을 수 없습니다: " + tokenName);
            }

            org.example.betty.contract.Token fanToken = org.example.betty.contract.Token.load(
                    fanTokenAddress,
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    zeroGasProvider
            );

            TransactionReceipt approveReceipt = fanToken.approve(exchangeAddress, amountWei).send();
            log.info("[APPROVE SUCCESS] FanToken -> Exchange, token={}, amount={}, txHash={}",
                    tokenName, amountFan, approveReceipt.getTransactionHash());

            TransactionReceipt sellReceipt = exchangeContract.sell(tokenName, amountWei).send();
            log.info("[SELL SUCCESS] token={}, amount={}, txHash={}",
                    tokenName, amountFan, sellReceipt.getTransactionHash());

            // 이벤트에서 amountOut 파싱
            List<Exchange.SellExecutedEventResponse> events = Exchange.getSellExecutedEvents(sellReceipt);
            if (events.isEmpty()) {
                throw new RuntimeException("SellExecuted 이벤트가 없습니다.");
            }

            BigInteger amountOut = events.get(0).amountOut;
            transaction.updateAmountOut(new BigDecimal(amountOut));

            // 트랜잭션 업데이트
            transaction.updateStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            // 잔고 동기화
            balanceService.syncWalletBalance(transaction.getWallet(), tokenName, fanTokenAddress);
            balanceService.syncWalletBalance(transaction.getWallet(), "BET", betTokenAddress);
        } catch (Exception e) {
            log.error("[SELL TRANSACTION FAILED] wallet={}, reason={}", transaction.getWallet().getWalletAddress(), e.getMessage(), e);
            transaction.updateStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
            String tokenFromName = transaction.getTokenFrom().getTokenName();
            String tokenToName = transaction.getTokenTo().getTokenName();
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
            Exchange contract = Exchange.load(
                    exchangeAddress,
                    web3jService.getWeb3j(),
                    new RawTransactionManager(
                            web3jService.getWeb3j(),
                            web3jService.getCredentials(),
                            web3jService.getChainId()
                    ),
                    new DefaultGasProvider());
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

    // 7. Swap 환율 조회
    @Override
    public SwapEstimateResponse getSwapAmount(String fromTokenName, String toTokenName, BigDecimal amountIn) {
        Token fromToken = tokenRepository.findByTokenName(fromTokenName)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        Token toToken = tokenRepository.findByTokenName(toTokenName)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        List<TokenPrice> fromPrices = tokenPriceRepository.findAllByTokenOrderByUpdatedAtDesc(fromToken);
        List<TokenPrice> toPrices = tokenPriceRepository.findAllByTokenOrderByUpdatedAtDesc(toToken);

        if (fromPrices.isEmpty() || toPrices.isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_PRICE_NOT_FOUND);
        }

        BigDecimal fromPrice = fromPrices.get(0).getPrice();
        BigDecimal toPrice = toPrices.get(0).getPrice();

        // 환율 계산 및 예상 수량 계산
        BigDecimal rate = fromPrice.divide(toPrice, 8, RoundingMode.HALF_UP);
        BigDecimal expectedAmount = amountIn.multiply(rate).setScale(8, RoundingMode.HALF_UP);

        return new SwapEstimateResponse(expectedAmount, rate);
    }

}
