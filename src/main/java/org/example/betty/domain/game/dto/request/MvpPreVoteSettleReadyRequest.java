package org.example.betty.domain.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MvpPreVoteSettleReadyRequest {
    private Long gameId;
    private List<Long> playerIds;
    private List<String> tokenAddresses;
}
