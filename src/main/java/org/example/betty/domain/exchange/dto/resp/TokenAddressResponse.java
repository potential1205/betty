package org.example.betty.domain.exchange.dto.resp;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenAddressResponse {

    private String tokenAddress;

    public static TokenAddressResponse of(String tokenAddress) {
        return TokenAddressResponse.builder().tokenAddress(tokenAddress).build();
    }
}
