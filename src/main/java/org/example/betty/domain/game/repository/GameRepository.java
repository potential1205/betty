package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGameDate(LocalDate gameDate);

    Optional<Game> findByGameDateAndSeasonAndHomeTeam_TeamCodeAndAwayTeam_TeamCode(
            LocalDate gameDate, Integer season, String homeTeamCode, String awayTeamCode);



}
