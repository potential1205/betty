package org.example.betty.domain.exchange.repository;


import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface TokenPriceRepository extends JpaRepository<TokenPrice, Long> {
    List<TokenPrice> findAllByTokenOrderByUpdatedAtDesc(Token token);

    BigDecimal findByTokenName(String tokenName);
}
