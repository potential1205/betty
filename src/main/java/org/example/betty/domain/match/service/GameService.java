package org.example.betty.domain.match.service;

import org.example.betty.domain.match.dto.resp.GameResponse;

public interface GameService {
    GameResponse findMatchById(Long gameId);
}
