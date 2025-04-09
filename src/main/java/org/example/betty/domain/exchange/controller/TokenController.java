package org.example.betty.domain.exchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.betty.domain.exchange.dto.resp.TokenAddressResponse;
import org.example.betty.domain.exchange.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TokenAddressResponse> getTokenNameByTeamId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long teamId) {

        String tokenAddress = tokenService.getTokenNameByTeamId(accessToken, teamId);

        return ResponseEntity.ok(TokenAddressResponse.of(tokenAddress));
    }
}
