package org.example.betty.domain.exchange.service;

public interface TokenService {
    String getTokenAddressByTeamId(String accessToken, Long teamId);

    String getTokenNameByTeamId(String accessToken, Long teamId);
}
