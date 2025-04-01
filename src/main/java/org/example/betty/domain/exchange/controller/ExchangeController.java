package org.example.betty.domain.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.example.betty.domain.exchange.entity.Transaction;
import org.example.betty.domain.exchange.service.ExchangeService;
import org.example.betty.domain.exchange.service.TransactionQueryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final TransactionQueryService transactionQueryService;

    @PostMapping("/add")
    public ResponseEntity<TransactionResponse> add(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                   @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @PostMapping("/remove")
    public ResponseEntity<TransactionResponse> remove(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                      @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @PostMapping("/buy")
    public ResponseEntity<TransactionResponse> buy(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                   @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @PostMapping("/sell")
    public ResponseEntity<TransactionResponse> sell(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                    @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(exchangeService.processTransaction(request, accessToken));
    }

    @PostMapping("/swap")
    public ResponseEntity<TransactionResponse> swap(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                    @RequestBody SwapRequest request) {
        return ResponseEntity.ok(exchangeService.processSwap(request, accessToken));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return ResponseEntity.ok(transactionQueryService.getTransactions(accessToken));
    }
}
