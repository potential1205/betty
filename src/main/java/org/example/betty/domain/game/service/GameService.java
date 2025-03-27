package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.response.GameInfoResponse;

import java.util.List;

public interface GameService {
    List<GameInfoResponse> selectDailyGames();
    void crawlLiveGameInfo();
}