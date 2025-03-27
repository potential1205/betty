package org.example.betty.domain.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.wallet.dto.CheckWalletNicknameResponse;
import org.example.betty.domain.wallet.dto.RegisterWalletRequest;
import org.example.betty.domain.wallet.service.WalletService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "지갑 조회", description = "accessToken을 이용해 현재 로그인된 사용자의 지갑이 등록되어 있는지 확인하고 닉네임을 반환합니다.")
    @GetMapping
    public ResponseEntity<CheckWalletNicknameResponse> retrieveWallet(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        String walletNickname = walletService.retrieveWallet(accessToken);

        return ResponseEntity.ok()
                .body(CheckWalletNicknameResponse.of(walletNickname));
    }

    @Operation(summary = "지갑 등록", description = "accessToken을 이용해 사용자의 지갑을 등록합니다. 닉네임은 중복될 수 없습니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse> registerWallet(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody RegisterWalletRequest registerWalletRequest) {

        walletService.registerWallet(accessToken, registerWalletRequest.getNickname());

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }
}
