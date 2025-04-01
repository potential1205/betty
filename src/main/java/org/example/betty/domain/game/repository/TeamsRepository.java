package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamsRepository extends JpaRepository<Teams, Long> {
    Teams findByTeamNameContaining(String teamName);
}
