package org.example.betty.domain.proposal.repository;

import org.example.betty.domain.proposal.entity.WalletProposals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletProposalRepository extends JpaRepository<WalletProposals, Long> {
}
