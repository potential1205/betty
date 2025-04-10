package org.example.betty.domain.exchange.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoResponse {
    private BigDecimal balance;
    private String tokenAddress;
    private String tokenName;

    public static TokenInfoResponse of(BigDecimal balance, String tokenAddress, String tokenName) {
        return TokenInfoResponse.builder()
                .balance(balance)
                .tokenAddress(tokenAddress)
                .build();
    }
}
