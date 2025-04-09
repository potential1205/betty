package org.example.betty.domain.game.dto.request;

import lombok.Getter;

@Getter
public class SubmitLiveVoteRequest {
    private Long gameId;
    private String selectedAnswer;
    private String problemId;
    private Long homeTeamId;
    private Long awayTeamId;
    private Long myTeamId;
}
