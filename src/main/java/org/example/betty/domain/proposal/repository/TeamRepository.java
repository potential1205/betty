package org.example.betty.domain.proposal.repository;

import org.example.betty.domain.game.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
