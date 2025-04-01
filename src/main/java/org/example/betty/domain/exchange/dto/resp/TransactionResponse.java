package org.example.betty.domain.exchange.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private boolean success; // 거래 성공 여부
    private String message; // 거래 결과 메시지
    private Long transactionId; // 트랜잭션 ID
}