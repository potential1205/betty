package org.example.betty.domain.wallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.contract.Token;
import org.example.betty.domain.exchange.entity.FanToken;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.wallet.repository.WalletBalanceRepository;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.common.util.Web3ContractUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ReadonlyTransactionManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final Web3j web3j;
    private final Web3ContractUtil contractUtil;
    private final WalletRepository walletRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final TokenRepository tokenRepository;

    @Value("${BET_ADDRESS}") private String BET_ADDRESS;
    @Value("${DSB_ADDRESS}") private String DSB_ADDRESS;
    @Value("${SSG_ADDRESS}") private String SSG_ADDRESS;
    @Value("${LGT_ADDRESS}") private String LGT_ADDRESS;
    @Value("${LTG_ADDRESS}") private String LTG_ADDRESS;
    @Value("${KWH_ADDRESS}") private String KWH_ADDRESS;
    @Value("${NCD_ADDRESS}") private String NCD_ADDRESS;
    @Value("${KTW_ADDRESS}") private String KTW_ADDRESS;
    @Value("${KIA_ADDRESS}") private String KIA_ADDRESS;
    @Value("${SSL_ADDRESS}") private String SSL_ADDRESS;
    @Value("${HWE_ADDRESS}") private String HWE_ADDRESS;

    @Override
    public void syncAllWalletBalances() {
        walletRepository.findAll().forEach(wallet -> {
            syncWalletBalance(wallet, "BET", BET_ADDRESS);
            syncWalletBalance(wallet, "DSB", DSB_ADDRESS);
            syncWalletBalance(wallet, "SSG", SSG_ADDRESS);
            syncWalletBalance(wallet, "LGT", LGT_ADDRESS);
            syncWalletBalance(wallet, "LTG", LTG_ADDRESS);
            syncWalletBalance(wallet, "KWH", KWH_ADDRESS);
            syncWalletBalance(wallet, "NCD", NCD_ADDRESS);
            syncWalletBalance(wallet, "KTW", KTW_ADDRESS);
            syncWalletBalance(wallet, "KIA", KIA_ADDRESS);
            syncWalletBalance(wallet, "SSL", SSL_ADDRESS);
            syncWalletBalance(wallet, "HWE", HWE_ADDRESS);
        });
    }

    private void syncWalletBalance(Wallet wallet, String tokenName, String tokenAddress) {
        try {
            ReadonlyTransactionManager txManager = new ReadonlyTransactionManager(web3j, wallet.getWalletAddress());
            Token contract = Token.load(tokenAddress, web3j, txManager, null);

            BigInteger rawBalance = contract.balanceOf(wallet.getWalletAddress()).send();
            BigDecimal balance = new BigDecimal(rawBalance);

            Optional<FanToken> tokenOpt = tokenRepository.findByTokenName(tokenName);
            if (tokenOpt.isEmpty()) {
                log.warn("Token not found in DB: {}", tokenName);
                return;
            }

            WalletBalance walletBalance = walletBalanceRepository.findByWalletAndToken(wallet, tokenOpt.get())
                    .orElse(WalletBalance.builder()
                            .wallet(wallet)
                            .fanToken(tokenOpt.get())
                            .build());

            walletBalance.setBalance(balance);
            walletBalance.setUpdatedAt(LocalDateTime.now());

            walletBalanceRepository.save(walletBalance);

            log.info("[BALANCE SYNCED] {} - {}: {}", wallet.getWalletAddress(), tokenName, balance);
        } catch (Exception e) {
            log.error("[BALANCE SYNC FAILED]: {} - {}", wallet.getWalletAddress(), tokenName, e.getMessage());
        }
    }
}