package org.example.betty.domain.proposal.repository;

import org.example.betty.domain.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findAllByTeamId(Long teamId);

    Proposal findByTeamIdAndProposalId(Long teamId, Long proposalId);
}
