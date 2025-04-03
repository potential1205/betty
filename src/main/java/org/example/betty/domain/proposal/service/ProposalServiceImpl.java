package org.example.betty.domain.proposal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.WalletBalance;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.exchange.repository.WalletBalanceRepository;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.proposal.dto.req.CreateProposalRequest;
import org.example.betty.domain.proposal.dto.req.CreateWalletProposalRequest;
import org.example.betty.domain.proposal.entity.Proposal;
import org.example.betty.domain.proposal.entity.WalletProposals;
import org.example.betty.domain.proposal.repository.ProposalRepository;
import org.example.betty.domain.proposal.repository.TeamRepository;
import org.example.betty.domain.proposal.repository.WalletProposalRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final WalletProposalRepository walletProposalRepository;

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
    @Transactional
    public void createProposal(CreateProposalRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token token = tokenRepository.findByTokenName(team.getTokenName())
                .orElseThrow(()-> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        WalletBalance walletBalance = walletBalanceRepository.findByWalletIdAndTokenId(wallet.getId(), token.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET_BALANCE));

        if (walletBalance.getBalance().compareTo(BigDecimal.TEN) < 0) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_TOKEN);
        }

        walletBalance.setBalance(walletBalance.getBalance().subtract(new BigDecimal("10")));

        // 토큰 10개 소각 로직 (온 체인)

        Proposal proposal = Proposal.builder()
                .teamId(team.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .targetCount(100)
                .currentCount(0)
                .createdAt(LocalDateTime.now())
                .closedAt(LocalDateTime.now().plusDays(7))
                .build();

        proposalRepository.save(proposal);
    }

    @Override
    @Transactional
    public void createWalletProposal(CreateWalletProposalRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token token = tokenRepository.findByTokenName(team.getTokenName())
                .orElseThrow(()-> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        WalletBalance walletBalance = walletBalanceRepository.findByWalletIdAndTokenId(wallet.getId(), token.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET_BALANCE));

        if (walletBalance.getBalance().compareTo(BigDecimal.ONE) < 0) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_TOKEN);
        }

        walletBalance.setBalance(walletBalance.getBalance().subtract(new BigDecimal("1")));

        // 토큰 1개 소각 (온 체인)

        Proposal proposal = proposalRepository.findByIdAndTeamId(request.getProposalId(), request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PROPOSAL));

        proposal.setCurrentCount(proposal.getCurrentCount() + 1);

        WalletProposals walletProposal = WalletProposals.builder()
                .proposalId(proposal.getId())
                .walletId(wallet.getId())
                .createdAt(LocalDateTime.now())
                .build();

        walletProposalRepository.save(walletProposal);
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

        return proposalRepository.findByIdAndTeamId(teamId, proposalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PROPOSAL));
    }
}
