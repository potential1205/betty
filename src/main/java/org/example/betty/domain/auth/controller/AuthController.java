package org.example.betty.domain.auth.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECPoint;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.auth.dto.req.LoginRequest;
import org.example.betty.domain.auth.service.AuthService;
import org.example.betty.domain.auth.service.TokenService;
import org.example.betty.domain.auth.service.Web3AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.login(loginRequest.getIdToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(SuccessResponse.of(true));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        authService.logout(accessToken);

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }
}