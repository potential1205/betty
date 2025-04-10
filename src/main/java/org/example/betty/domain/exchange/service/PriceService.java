package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.entity.TokenPrice;

import java.math.BigDecimal;
import java.util.List;

public interface PriceService {
    void syncAllPrices();
    BigDecimal getPriceByTokenName(String tokenName);
    BigDecimal getPriceByTokenId(Long tokenId);


}
