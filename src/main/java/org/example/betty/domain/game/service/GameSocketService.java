package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.live.RedisGameProblem;

public interface GameSocketService {
    void sendGameEvent(Long gameId, String inning, String score);
    void sendGameProblem(Long gameId, RedisGameProblem redisGameProblem);

}
