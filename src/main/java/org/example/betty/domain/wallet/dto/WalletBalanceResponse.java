package org.example.betty.domain.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class WalletBalanceResponse {
    private String tokenName;
    private BigDecimal balance;
}
