package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.dto.resp.TokenInfoResponse;

import java.math.BigDecimal;

public interface TokenService {
    String getTokenAddressByTeamId(String accessToken, Long teamId);

    String getTokenNameByTeamId(String accessToken, Long teamId);

    BigDecimal getTokenPriceByTeamId(String accessToken, Long teamId);

    BigDecimal getBettyPrice(String accessToken);

    TokenInfoResponse getTokenInfoById(String accessToken, Long tokenId);
}
