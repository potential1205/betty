package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.FanToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<FanToken, Long> {
    Optional<FanToken> findByTokenName(String tokenName);
}
