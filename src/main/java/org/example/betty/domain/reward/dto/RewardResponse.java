package org.example.betty.domain.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardResponse {
    private boolean success;
    private String message;
    private String transactionHash;
}