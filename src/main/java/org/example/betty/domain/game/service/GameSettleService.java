package org.example.betty.domain.game.service;

public interface GameSettleService {

    void liveVoteSettle(Long gameId, Long homeTeamId, Long awayTeamId);
    void preVoteTeamSettle(Long gameId, Long winnderTeamId);
    void createPreVoteTeamSettle(Long gameId, Long teamAId, Long teamBId, Long startTime, String teamATokenAddress, String teamBTokenAddress);
}
