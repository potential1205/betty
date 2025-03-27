package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Teams, Long> {
    Teams findByTeamNameContaining(String teamName);
}
