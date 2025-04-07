package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;

import java.util.List;

public interface GameService {

    List<RedisGameSchedule> getTodayGameSchedules();

    RedisGameLineup getGameLineup(String gameId);

    String getGameStatus(String gameId);
}
