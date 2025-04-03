package org.example.betty.domain.proposal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "proposals")
public class Proposal {
    @Id
    private Long id;

    private Long teamId;

    private String title;

    private String content;

    private int targetCount;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime closedAt;
}
