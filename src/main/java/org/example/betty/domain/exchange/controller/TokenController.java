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

    @Operation(summary = "트랜잭션 조회", description = "현재 로그인된 사용자의 전체 트랜잭션 기록을 조회합니다.")
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TokenAddressResponse> getTokenAddressByTeamId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long teamId) {

        String tokenAddress = tokenService.getTokenAddressByTeamId(accessToken, teamId);

        return ResponseEntity.ok(TokenAddressResponse.of(tokenAddress));
    }
}
