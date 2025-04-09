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
public class TokenPriceResponse {
    private BigDecimal price;

    public static TokenPriceResponse of(BigDecimal price) {
        return TokenPriceResponse.builder()
                .price(price)
                .build();
    }
}
