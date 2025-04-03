package org.example.betty.domain.proposal.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.WalletBalance;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.exchange.repository.WalletBalanceRepository;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.proposal.entity.Proposal;
import org.example.betty.domain.proposal.repository.ProposalRepository;
import org.example.betty.domain.proposal.repository.TeamRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final ProposalRepository proposalRepository;
    private final TeamRepository teamRepository;
    private final TokenRepository tokenRepository;
    private final WalletBalanceRepository walletBalanceRepository;

    @Override
    public BigDecimal getTeamTokenCount(Long teamId, String accessToken) {
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
    public List<Proposal> getProposalList(Long teamId, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        return proposalRepository.findAllByTeamId(teamId);
    }

    @Override
    public Proposal getProposal(Long teamId, Long proposalId, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        return proposalRepository.findByIdAndTeamId(teamId, proposalId);
    }
}
