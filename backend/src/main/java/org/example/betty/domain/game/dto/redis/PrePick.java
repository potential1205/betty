package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrePick {
    private String teamPick;
    private BigDecimal teamPickBetAmount;

    private String mvpPick;
    private String mvpTeam;
    private BigDecimal mvpPickBetAmount;

    private LocalDateTime createdAt;
}
