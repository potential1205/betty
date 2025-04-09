package org.example.betty.domain.exchange.service;

import java.math.BigDecimal;

public interface TokenService {
    String getTokenAddressByTeamId(String accessToken, Long teamId);

    String getTokenNameByTeamId(String accessToken, Long teamId);

    BigDecimal getTokenPriceByTeamId(String accessToken, Long teamId);

    BigDecimal getBettyPrice(String accessToken);
}
