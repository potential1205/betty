package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.wallet.repository.WalletBalanceRepository;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final SessionUtil sessionUtil;
    private final TeamRepository teamRepository;
    private final WalletRepository walletRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final TokenRepository tokenRepository;

    @Override
    public String getTokenAddressByTeamId(String accessToken, Long teamId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token token = tokenRepository.findByTokenName(team.getTokenName())
                .orElseThrow(()-> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        return token.getTokenAddress();
    }

    @Override
    public String getTokenNameByTeamId(String accessToken, Long teamId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token token = tokenRepository.findByTokenName(team.getTokenName())
                .orElseThrow(()-> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        return token.getTokenName();
    }

    @Override
    public BigDecimal getTokenPriceByTeamId(String accessToken, Long teamId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token token = tokenRepository.findByTokenName(team.getTokenName())
                .orElseThrow(()-> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        WalletBalance walletBalance = walletBalanceRepository.findByWalletIdAndTokenId(wallet.getId(), token.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET_BALANCE));

        return walletBalance.getBalance();
    }

    @Override
    public BigDecimal getBettyPrice(String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        WalletBalance walletBalance = walletBalanceRepository.findByWalletIdAndTokenId(wallet.getId(), 1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET_BALANCE));

        return walletBalance.getBalance();
    }
}
