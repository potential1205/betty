package org.example.betty.domain.exchange.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SwapEstimateResponse {
    private BigDecimal expectedAmount;
    private BigDecimal rate;
}