package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.LiquidityPool;
import org.example.betty.domain.exchange.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiquidityPoolRepository extends JpaRepository<LiquidityPool, Long> {
    Optional<LiquidityPool> findByToken(Token token);
}
