package org.example.betty.domain.display.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayAccess {
    @Id
    private Long id;

    @NotNull
    private Long teamId;

    @NotNull
    private Long gameId;

    @NotNull
    private String walletAddress;
}
