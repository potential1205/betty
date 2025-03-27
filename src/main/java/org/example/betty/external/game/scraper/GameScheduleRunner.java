package org.example.betty.external.game.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.entity.Games;
import org.example.betty.domain.game.entity.Teams;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.external.game.scraper.dto.GameSchedule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameScheduleRunner implements CommandLineRunner {

    private final GameScheduleScraper scraper;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) {
        if (gameRepository.count() > 0) {
            return;
        }
        List<GameSchedule> schedules = scraper.scrapeAllMonthsSchedule();

        List<Games> games = schedules.stream()
                .map(this::toEntity)
                .toList();

        gameRepository.saveAll(games);
    }

    private Games toEntity(GameSchedule schedule) {
        Teams homeTeam = teamRepository.findByTeamNameContaining(schedule.getHomeTeam());
        Teams awayTeam = teamRepository.findByTeamNameContaining(schedule.getAwayTeam());

        return new Games(
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
        switch (rawStatus) {
            case "예정":
                return "SCHEDULED";
            case "진행":
                return "LIVE";
            case "종료":
                return "ENDED";
            case "취소":
                return "CANCELED";
            default:
                return "UNKNOWN";
        }
    }
}
