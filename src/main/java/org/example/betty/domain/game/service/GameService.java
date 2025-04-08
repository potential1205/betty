package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;
import org.example.betty.domain.game.entity.Game;


import java.util.List;

public interface GameService {

    List<GameInfoResponse> getTodayGameSchedules(String accessToken);

    RedisGameLineup getGameLineup(String accessToken, Long gameId);

    void updateGameStatusToLive(Game game);

    void updateGameStatusToEnded(Game game);

    Game findGameByGameId(String gameId);

    String getGameStatus(String accessToken, Long gameId);


}
