package org.example.betty.domain.proposal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.service.ExchangeService;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.domain.proposal.dto.req.SaveProposalHashRequest;
import org.example.betty.domain.proposal.dto.resp.CreateProposalResponse;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.wallet.repository.WalletBalanceRepository;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.proposal.dto.req.CreateProposalRequest;
import org.example.betty.domain.proposal.dto.req.CreateWalletProposalRequest;
import org.example.betty.domain.proposal.entity.Proposal;
import org.example.betty.domain.proposal.entity.WalletProposals;
import org.example.betty.domain.proposal.repository.ProposalRepository;
import org.example.betty.domain.proposal.repository.WalletProposalRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    private final ExchangeService exchangeService;

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
    public CreateProposalResponse createProposal(CreateProposalRequest request, String accessToken) {
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

        Proposal proposal = Proposal.builder()
                .walletId(wallet.getId())
                .teamId(team.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .targetCount(100)
                .currentCount(0)
                .createdAt(LocalDateTime.now())
                .closedAt(LocalDateTime.now().plusDays(7))
                .build();

        proposalRepository.save(proposal);

        String contentHash = generateKeccak256(request.getTeamId(), request.getTitle(), request.getContent(), proposal.getId());

        proposal.setContentHash(contentHash);

        // 토큰 10개 소각 로직 (온 체인)
//        TransactionRequest transactionRequest = new TransactionRequest(wallet.getId(), token.getId(), new BigDecimal("10"));
//        exchangeService.processUse(transactionRequest, accessToken);

        return CreateProposalResponse.of(proposal.getId(), contentHash, proposal.getClosedAt());
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

        Proposal proposal = proposalRepository.findByIdAndTeamId(request.getProposalId(), request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PROPOSAL));

        proposal.setCurrentCount(proposal.getCurrentCount() + 1);

        if (walletProposalRepository.existsByProposalIdAndWalletId(proposal.getId(), wallet.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_WALLET_PROPOSAL);
        }

        WalletProposals walletProposal = WalletProposals.builder()
                .proposalId(proposal.getId())
                .walletId(wallet.getId())
                .createdAt(LocalDateTime.now())
                .build();

        walletProposalRepository.save(walletProposal);

        // 토큰 1개 소각 (온 체인)
//        TransactionRequest transactionRequest = new TransactionRequest(wallet.getId(), token.getId(), new BigDecimal("1"));
//        exchangeService.processUse(transactionRequest, accessToken);
    }

    @Override
    public void saveProposalHash(SaveProposalHashRequest request, String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        Proposal proposal = proposalRepository.findByIdAndWalletId(request.getProposalId(), wallet.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TRANSACTION));

        proposal.setTxHash(request.getTxHash());
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

    public static String generateKeccak256(Long teamId, String title, String content, Long proposalId) {
        String input = teamId + "|" + title + "|" + content + "|" + proposalId;
        byte[] hashBytes = Hash.sha3(input.getBytes(StandardCharsets.UTF_8));
        return Numeric.toHexString(hashBytes);
    }
}
