package org.example.betty.domain.wallet.controller;

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

    @GetMapping
    public ResponseEntity<CheckWalletNicknameResponse> retrieveWallet(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        String walletNickname = walletService.retrieveWallet(accessToken);

        return ResponseEntity.ok()
                .body(CheckWalletNicknameResponse.of(walletNickname));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> registerWallet(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody RegisterWalletRequest registerWalletRequest) {

        walletService.registerWallet(accessToken, registerWalletRequest.getNickname());

        return ResponseEntity.ok()
                .body(SuccessResponse.of(true));
    }
}
