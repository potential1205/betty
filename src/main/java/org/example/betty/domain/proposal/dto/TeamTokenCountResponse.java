package org.example.betty.domain.proposal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamTokenCountResponse {

    private BigDecimal teamTokenCount;

    public static TeamTokenCountResponse of(BigDecimal teamTokenCount) {
        return TeamTokenCountResponse.builder()
                .teamTokenCount(teamTokenCount)
                .build();
    }
}
