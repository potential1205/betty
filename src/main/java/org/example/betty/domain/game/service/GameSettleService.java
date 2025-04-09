package org.example.betty.domain.game.service;

import java.util.List;

public interface GameSettleService {

    void liveVoteSettle(Long gameId);

    void createPreVoteTeamSettle(Long gameId, Long teamAId, Long teamBId, Long startTime, String teamATokenAddress, String teamBTokenAddress);
    void preVoteTeamSettle(Long gameId, Long winnderTeamId);

    void createPreVoteMVPSettle(Long gameId, List<Long> playerIds, List<String> tokenAddresses, Long startTime);
    void preVoteMVPSettle(Long gameId, Long winningPlayerId);
}
