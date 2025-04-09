package org.example.betty.domain.exchange.repository;


import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenPriceRepository extends JpaRepository<TokenPrice, Long> {
    List<TokenPrice> findAllByTokenOrderByUpdatedAtDesc(Token token);

    Optional<TokenPrice> findByTokenName(String tokenName);

    Optional<Object> findFirstByTokenNameOrderByUpdatedAtDesc(String tokenName);
}
