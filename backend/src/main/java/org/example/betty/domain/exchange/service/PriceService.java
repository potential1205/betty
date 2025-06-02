package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.entity.TokenPrice;

import java.math.BigDecimal;
import java.util.List;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface PriceService {
    void syncAllPrices();
    BigDecimal getPriceByTokenName(String tokenName);
    BigDecimal getPriceByTokenId(Long tokenId);

    // 전체 토큰 가격 조회
    public List<TokenPrice> getAllTokenPrices();
//    void syncHourlyPrices();


}
