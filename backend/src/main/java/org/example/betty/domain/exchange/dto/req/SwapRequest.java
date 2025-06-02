package org.example.betty.domain.exchange.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwapRequest {
    private Long walletId;
    private Long tokenFromId; // 스왑할 팬토큰 A의 ID
    private Long tokenToId; // 스왑할 팬토큰 B의 ID
    private BigDecimal amountIn; // 스왑할 팬토큰 A의 수량
}