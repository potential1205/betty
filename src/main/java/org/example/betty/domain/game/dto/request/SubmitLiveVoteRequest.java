package org.example.betty.domain.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitLiveVoteRequest {
    private Long gameId;
    private String selectedAnswer;
    private String problemId;
}
