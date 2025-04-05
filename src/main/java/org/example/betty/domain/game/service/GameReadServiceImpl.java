package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GamesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameReadServiceImpl implements GameReadService {

    private final GamesRepository gamesRepository;

    @Override
    public List<RedisGameSchedule> getTodayGameSchedules() {
        LocalDate today = LocalDate.now();

        List<Game> games = gamesRepository.findByGameDate(today);
        List<RedisGameSchedule> schedules = new ArrayList<>();

        for (Game game : games) {
            RedisGameSchedule schedule = RedisGameSchedule.builder()
                    .season(game.getSeason())
                    .gameDate(game.getGameDate().toString())
                    .startTime(game.getStartTime().toString())
                    .stadium(game.getStadium())
                    .homeTeam(game.getHomeTeam().getTeamName().split(" ")[0])
                    .awayTeam(game.getAwayTeam().getTeamName().split(" ")[0])
                    .status(game.getStatus())
                    .build();
            schedules.add(schedule);
        }

        log.info("[GameReadService] DB에서 {}개 경기 일정 조회", schedules.size());
        return schedules;
    }
}
