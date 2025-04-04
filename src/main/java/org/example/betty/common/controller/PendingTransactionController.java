package org.example.betty.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.common.util.PendingTransactionUtil;
import org.example.betty.common.util.SessionUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/validate")
@RequiredArgsConstructor
public class PendingTransactionController {

    private final SessionUtil sessionUtil;
    private final PendingTransactionUtil pendingTransactionUtil;

    @Operation(
            summary = "트랜잭션 처리 여부 확인",
            description = "현재 지갑에 PENDING 상태의 트랜잭션이 존재하는지 확인합니다."
    )
    @GetMapping("/pending")
    public ResponseEntity<SuccessResponse> validatePending(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);
        boolean hasPending = pendingTransactionUtil.hasPending(walletAddress);
        return ResponseEntity.ok(SuccessResponse.of(!hasPending));
    }
}
