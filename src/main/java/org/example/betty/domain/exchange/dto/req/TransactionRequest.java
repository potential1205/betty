package org.example.betty.domain.exchange.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Long walletId;
    private Long tokenId; // 팬토큰 ID (Add, Remove 시에는 null 가능)
    private BigDecimal amountIn; // 거래할 금액 또는 수량
}