package org.example.betty.domain.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class WalletInfoResponse {
    private String walletAddress;
    private String nickname;
    private BigDecimal totalBet;
    private List<WalletBalanceResponse> tokens;
}
