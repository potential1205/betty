package org.example.betty.domain.proposal.service;

import org.example.betty.domain.proposal.dto.req.CreateProposalRequest;
import org.example.betty.domain.proposal.dto.req.CreateWalletProposalRequest;
import org.example.betty.domain.proposal.entity.Proposal;

import java.math.BigDecimal;
import java.util.List;

public interface ProposalService {
    List<Proposal> getProposalList(Long teamId, String accessToken);

    Proposal getProposal(Long teamId, Long proposalId, String accessToken);

    BigDecimal getTeamTokenCount(Long teamId, String accessToken);

    void createProposal(CreateProposalRequest request, String accessToken);

    void createWalletProposal(CreateWalletProposalRequest request, String accessToken);
}
