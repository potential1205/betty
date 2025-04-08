package org.example.betty.domain.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequest {
    private String tokenAddress;
    private String recipient;
    private BigInteger amount;
}