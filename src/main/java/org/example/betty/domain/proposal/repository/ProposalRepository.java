package org.example.betty.domain.proposal.repository;

import org.example.betty.domain.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findAllByTeamId(Long teamId);

    Optional<Proposal> findByIdAndTeamId(Long proposalId, Long teamId);

    Optional<Proposal> findByIdAndWalletId(Long proposalId, Long id);
}
