package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.resp.GameResponse;

public interface GameService {
    GameResponse findMatchById(Long gameId);
}
