package org.example.betty.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.external.game.scraper.GameScheduleScraper;
import org.example.betty.external.game.scraper.dto.GameSchedule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class GamesDataLoader implements CommandLineRunner {

    private final GameScheduleScraper scraper;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) {
        if (gameRepository.count() > 0) {
            return;
        }

        if (teamRepository.count() == 0) {
            return;
        }

        List<GameSchedule> schedules = scraper.scrapeAllMonthsSchedule();
        List<Game> games = schedules.stream()
                .map(this::toEntity)
                .toList();

        gameRepository.saveAll(games);
    }

    private Game toEntity(GameSchedule schedule) {
        Team homeTeam = teamRepository.findByTeamNameContaining(schedule.getHomeTeam());
        Team awayTeam = teamRepository.findByTeamNameContaining(schedule.getAwayTeam());

        return new Game(
                homeTeam,
                awayTeam,
                schedule.getStadium(),
                Integer.parseInt(schedule.getSeason()),
                LocalDate.parse(schedule.getGameDate()),
                LocalTime.parse(schedule.getStartTime()),
                convertStatus(schedule.getStatus())
        );
    }

    private String convertStatus(String rawStatus) {
        return switch (rawStatus) {
            case "예정" -> "SCHEDULED";
            case "종료" -> "ENDED";
            case "취소" -> "CANCELED";
            default -> "LIVE";
        };
    }
}
