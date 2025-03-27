package org.example.betty.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.auth.dto.req.LoginRequest;
import org.example.betty.domain.auth.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "클라이언트에서 전달한 ID 토큰을 통해 access token(JWT)을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.login(loginRequest.getIdToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(SuccessResponse.of(true));
    }


    @Operation(summary = "로그아웃", description = "Authorization 헤더에 담긴 access token을 통해 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        authService.logout(accessToken);

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }
}