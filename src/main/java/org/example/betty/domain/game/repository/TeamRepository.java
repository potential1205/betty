package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByTeamNameContaining(String teamName);
    Optional<Team> findByTeamCode(String teamCode);
    Optional<Team> findByTeamNameStartingWith(String teamName);

}
