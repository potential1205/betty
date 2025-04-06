package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameRelay;

public interface GameProblemService {
    void handleRelayUpdate(String gameId, RedisGameRelay relay);
}
