package org.example.betty.domain.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MvpPreVoteSettleRequest {
    private Long gameId;
    private Long winningPlayerId;
}
