package org.example.betty.domain.game.dto.redis;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PreVoteAnswer {

    private String teamA; // 홈팀
    private String teamB; // 원정팀

    private String winnerTeam;
    private String loserTeam;

    private String mvpName;
    private String mvpTeam;
}
