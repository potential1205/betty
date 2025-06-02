package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.SwapEstimateResponse;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;

public interface ExchangeService {

    TransactionResponse processBuy(TransactionRequest request, String accessToken);
    TransactionResponse processSell(TransactionRequest request, String accessToken);
    TransactionResponse processSwap(SwapRequest request, String accessToken);
    TransactionResponse processUse(TransactionRequest request, String accessToken);
    TransactionResponse processAdd(TransactionRequest request, String accessToken);
    TransactionResponse processRemove(TransactionRequest request, String accessToken);
    SwapEstimateResponse getSwapAmount(String fromTokenName, String toTokenName, BigDecimal amountIn);
}
