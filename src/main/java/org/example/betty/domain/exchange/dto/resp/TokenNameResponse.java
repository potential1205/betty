package org.example.betty.domain.exchange.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenNameResponse {
    private String tokenName;

    public static TokenNameResponse of(String tokenName) {
        return TokenNameResponse.builder().tokenName(tokenName).build();
    }
}
