package org.example.betty.domain.exchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.SwapEstimateResponse;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.service.ExchangeService;
import org.example.betty.domain.exchange.service.TransactionQueryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
@Tag(name = "거래소", description = "베티코인 및 팬토큰 거래 처리")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final TransactionQueryService transactionQueryService;

    @Operation(summary = "베티코인 충전", description = "원화를 사용해 베티코인을 충전합니다.")
    @PostMapping("/add")
    public ResponseEntity<TransactionResponse> add(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processAdd(request, accessToken));
    }

    @Operation(summary = "베티코인 출금", description = "베티코인을 원화로 출금합니다.")
    @PostMapping("/remove")
    public ResponseEntity<TransactionResponse> remove(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                      @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @Operation(summary = "팬토큰 구매", description = "베티코인으로 특정 팬토큰을 구매합니다.")
    @PostMapping("/buy")
    public ResponseEntity<TransactionResponse> buy(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                   @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @Operation(summary = "팬토큰 판매", description = "보유한 팬토큰을 판매해 베티코인을 획득합니다.")
    @PostMapping("/sell")
    public ResponseEntity<TransactionResponse> sell(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                    @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @Operation(summary = "팬토큰 스왑", description = "팬토큰 A를 팬토큰 B로 교환합니다.")
    @PostMapping("/swap")
    public ResponseEntity<TransactionResponse> swap(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                    @RequestBody SwapRequest request) {
        return ResponseEntity.ok(exchangeService.processSwap(request, accessToken));
    }

    @Operation(summary = "트랜잭션 조회", description = "현재 로그인된 사용자의 전체 트랜잭션 기록을 조회합니다.")
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return ResponseEntity.ok(transactionQueryService.getTransactions(accessToken));
    }

    @Operation(summary = "스왑 예상 금액 계산", description = "입력한 팬토큰 수량에 따른 스왑 예상 금액과 환율을 계산합니다.")
    @GetMapping("/estimate")
    public ResponseEntity<SwapEstimateResponse> estimateSwapAmount(@RequestParam String fromToken,
                                                                   @RequestParam String toToken,
                                                                   @RequestParam BigDecimal amountIn) {
        return ResponseEntity.ok(exchangeService.getSwapAmount(fromToken, toToken, amountIn));
    }

}
