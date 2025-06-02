package org.example.betty.common.resp;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {

    private int code;           // 0: 성공, 1: 실패
    private String message;     // 메시지
    private BigDecimal data;    // 실제 가격 데이터 (BigDecimal로 설정)

    // 가격을 반환하는 성공 응답
    public static PriceResponse success(BigDecimal data) {
        return new PriceResponse(0, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 가격이 없을 경우 null을 반환하는 실패 응답
    public static PriceResponse fail(String message) {
        return new PriceResponse(1, message, null);
    }
}
