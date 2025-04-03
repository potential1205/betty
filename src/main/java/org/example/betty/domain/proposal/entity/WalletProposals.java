package org.example.betty.domain.proposal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet_proposals")
public class WalletProposals {
    @Id
    private Long id;

    private Long proposalId;

    private Long walletId;

    private LocalDateTime createdAt;
}
