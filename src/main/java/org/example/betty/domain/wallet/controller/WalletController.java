package org.example.betty.domain.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.resp.BaseResponse;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.wallet.dto.CheckWalletNicknameResponse;
import org.example.betty.domain.wallet.dto.RegisterWalletRequest;
import org.example.betty.domain.wallet.dto.WalletBalanceResponse;
import org.example.betty.domain.wallet.dto.WalletInfoResponse;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.entity.WalletBalance;
import org.example.betty.domain.wallet.service.BalanceService;
import org.example.betty.domain.wallet.service.WalletService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final BalanceService balanceService;

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

    @PostMapping("/balances/sync")
    public BaseResponse<Void> syncAllWalletBalances() {
        balanceService.syncAllWalletBalances();
        return BaseResponse.success();
    }

    @Operation(summary = "지갑 잔고 조회", description = "현재 로그인한 사용자의 잔고와 토큰 목록을 반환합니다.")
    @GetMapping("/balances")
    public ResponseEntity<WalletInfoResponse> getWalletBalances(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {

        Wallet wallet = walletService.findByAccessToken(accessToken);
        List<WalletBalance> balances = balanceService.getAllByWallet(wallet);

        List<WalletBalanceResponse> tokenList = balances.stream()
                .map(b -> WalletBalanceResponse.builder()
                        .tokenName(b.getToken().getTokenName())
                        .balance(b.getBalance())
                        .build())
                .toList();

        BigDecimal total = tokenList.stream()
                .filter(t -> t.getTokenName().equals("BET"))
                .map(WalletBalanceResponse::getBalance)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        return ResponseEntity.ok(
                WalletInfoResponse.builder()
                        .walletAddress(wallet.getWalletAddress())
                        .nickname(wallet.getNickname())
                        .totalBet(total)
                        .tokens(tokenList)
                        .build()
        );
    }

}
