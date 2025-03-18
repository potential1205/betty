package org.example.betty.domain.match.repository;

import org.example.betty.domain.match.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
