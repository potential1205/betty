package org.example.betty.domain.game.service;

import java.util.List;

public interface GameSettleService {

    void liveVoteSettle(Long gameId);

    void createPreVoteTeamSettle(Long gameId, Long teamAId, Long teamBId);
    void preVoteTeamSettle(Long gameId, Long winnderTeamId);

    void createPreVoteMVPSettle(Long gameId, List<Long> playerIds, List<String> tokenAddresses);
    void preVoteMVPSettle(Long gameId, Long winningPlayerId);
}
