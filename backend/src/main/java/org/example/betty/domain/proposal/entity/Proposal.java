package org.example.betty.domain.proposal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "proposals")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long walletId;

    private Long teamId;

    private String title;

    private String content;

    private int targetCount;

    private int currentCount;

    private String txHash;

    private String contentHash;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime closedAt;
}
