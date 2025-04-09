package org.example.betty.domain.game.dto.redis;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PreVoteAnswer {

    private String teamA;
    private String teamB;

    private String winnerTeam;
    private String loserTeam;

    private String mvpName;
    private String mvpTeam;
}
