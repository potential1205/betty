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

    // gameId로부터 팀 코드(away, home)를 파싱하고, DB에서 각 팀의 PK(id)를 조회해 반환
    Map<String, Long> resolveTeamIdsFromGameId(String gameId);

    // DB에서 해당 조건을 만족하는 Game 객체의 PK(id)를 조회해 반환
    Long resolveGameDbId(String gameId);


}
