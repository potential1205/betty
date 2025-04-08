package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreVoteAnswer {

    private String teamA;
    private String teamB;

    private String winnerTeam;
    private String loserTeam;

    private String mvpName;
    private String mvpTeam;
}
