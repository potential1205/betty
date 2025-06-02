package org.example.betty.domain.reward.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.reward.dto.RewardRequest;
import org.example.betty.domain.reward.dto.RewardResponse;
import org.example.betty.domain.reward.service.RewardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reward")
public class RewardController {

    private final RewardService rewardService;

    @PostMapping
    public RewardResponse sendReward(@RequestBody RewardRequest request) {
        return rewardService.sendReward(request);
    }

    @GetMapping("/balance")
    public String getBalance(@RequestParam String tokenAddress) {
        return rewardService.getTokenBalance(tokenAddress);
    }
}
