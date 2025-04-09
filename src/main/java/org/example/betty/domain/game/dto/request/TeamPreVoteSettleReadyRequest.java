package org.example.betty.domain.game.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPreVoteSettleReadyRequest {
    private Long gameId;
    private Long teamAId;
    private Long teamBId;
    private Long startTime;
    private String teamATokenAddress;
    private String teamBTokenAddress;
}
