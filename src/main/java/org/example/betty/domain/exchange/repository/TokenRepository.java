package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenName(String tokenName);
}
