package org.example.betty.domain.reward.service;

import org.example.betty.domain.reward.dto.RewardRequest;
import org.example.betty.domain.reward.dto.RewardResponse;

public interface RewardService {
    RewardResponse sendReward(RewardRequest request);
    String getTokenBalance(String tokenAddress);
}