package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.dto.response.GameInfoResponse;

import java.util.List;

public interface GameReadService {

    List<RedisGameSchedule> getTodayGameSchedules();

    RedisGameLineup getGameLineup(String gameId);


}
