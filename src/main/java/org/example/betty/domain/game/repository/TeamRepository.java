package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByTeamNameContaining(String teamName);
}
