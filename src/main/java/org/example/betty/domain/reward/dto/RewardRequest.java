package org.example.betty.domain.reward.dto;

import lombok.Data;
import java.math.BigInteger;

@Data
public class RewardRequest {
    private String tokenAddress;
    private String recipient;
    private BigInteger amount;
}