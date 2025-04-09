package org.example.betty.domain.exchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.betty.domain.exchange.dto.resp.TokenAddressResponse;
import org.example.betty.domain.exchange.dto.resp.TokenNameResponse;
import org.example.betty.domain.exchange.dto.resp.TokenPriceResponse;
import org.example.betty.domain.exchange.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "팀 ID로 토큰 주소 조회", description = "팀 ID로 토큰 주소를 조회합니다.")
    @GetMapping("/address/teams/{teamId}")
    public ResponseEntity<TokenAddressResponse> getTokenAddressByTeamId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long teamId) {

        String tokenAddress = tokenService.getTokenAddressByTeamId(accessToken, teamId);

        return ResponseEntity.ok(TokenAddressResponse.of(tokenAddress));
    }

    @Operation(summary = "팀 ID로 토큰 이름 조회", description = "팀 ID로 토큰 이름을 조회합니다.")
    @GetMapping("/name/teams/{teamId}")
    public ResponseEntity<TokenNameResponse> getTokenNameByTeamId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long teamId) {

        String tokenName = tokenService.getTokenNameByTeamId(accessToken, teamId);

        return ResponseEntity.ok(TokenNameResponse.of(tokenName));
    }

    @Operation(summary = "팬 토큰 잔액 조회", description = "팀 ID로 팬 토큰 잔액을 조회합니다.")
    @GetMapping("/price/teams/{teamId}")
    public ResponseEntity<TokenPriceResponse> getTokenPriceByTeamId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long teamId) {

        BigDecimal balance = tokenService.getTokenPriceByTeamId(accessToken, teamId);

        return ResponseEntity.ok(TokenPriceResponse.of(balance));
    }

    @Operation(summary = "배티 코인 잔액 조회", description = "배티 코인 잔액을 조회합니다.")
    @GetMapping("/price/betty")
    public ResponseEntity<TokenPriceResponse> getBettyPrice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        BigDecimal balance = tokenService.getBettyPrice(accessToken);

        return ResponseEntity.ok(TokenPriceResponse.of(balance));
    }
}
