package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Game;


import java.util.List;
import java.util.Map;

public interface GameService {

    List<GameInfoResponse> getTodayGameSchedules(String accessToken);

    RedisGameLineup getGameLineup(String accessToken, Long gameId);

    void updateGameStatusToLive(Game game);

    void updateGameStatusToEnded(Game game);

    Game findGameByGameId(String gameId);

    String getGameStatus(String accessToken, Long gameId);

    // [사전베팅] - 승리팀 투표하기
    void betWinningTeam(String accessToken, Long gameId, Long teamId);

    // [사전베팅] - MVP 투표하기
    void betMvpPlayer(String accessToken, Long gameId, Long playerId);

    // 20250409HHOB02025 -> 원정팀 id, 홈팀 id
    Map<String, Long> resolveTeamIdsFromGameId(String gameId);

    // 20250409HHOB02025 -> id
    Long resolveGameDbId(String gameId);


}
