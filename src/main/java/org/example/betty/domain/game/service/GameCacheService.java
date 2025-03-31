package org.example.betty.domain.game.service;

import java.time.LocalDate;

public interface GameCacheService {

    void cacheDailyGames();

    boolean recoverTodayGameSchedule(LocalDate targetDate);

}
