package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.dto.req.SwapRequest;
import org.example.betty.domain.exchange.dto.req.TransactionRequest;
import org.example.betty.domain.exchange.dto.resp.TransactionResponse;

public interface ExchangeService {

    TransactionResponse processTransaction(TransactionRequest request, String accessToken);
    TransactionResponse processSwap(SwapRequest request, String accessToken);
    TransactionResponse processUse(TransactionRequest request, String accessToken);
    TransactionResponse processAdd(TransactionRequest request, String accessToken);
}
