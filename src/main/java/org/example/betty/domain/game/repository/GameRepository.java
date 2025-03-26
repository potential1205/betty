package org.example.betty.domain.game.repository;

import org.example.betty.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
