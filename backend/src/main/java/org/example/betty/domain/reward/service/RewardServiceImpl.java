package org.example.betty.domain.reward.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.contract.RewardPool;
import org.example.betty.domain.reward.dto.RewardRequest;
import org.example.betty.domain.reward.dto.RewardResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final Web3j web3j;

    @Value("${ADMIN_PRIVATE_KEY}")
    private String AdminPrivateKey;

    @Value("${REWARD_POOL}")
    private String rewardPool;

    @Override
    public RewardResponse sendReward(RewardRequest request) {
        try {
            Credentials credentials = Credentials.create(AdminPrivateKey);

            RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, 14609);
            RewardPool contract = RewardPool.load(
                    rewardPool, web3j, txManager, new DefaultGasProvider());

            var tx = contract.reward(
                    request.getTokenAddress(),
                    request.getRecipient(),
                    request.getAmount()
            ).send();

            return new RewardResponse(true, "Reward sent", tx.getTransactionHash());

        } catch (Exception e) {
            log.error("Reward failed", e);
            return new RewardResponse(false, "Reward failed: " + e.getMessage(), null);
        }
    }

    @Override
    public String getTokenBalance(String tokenAddress) {
        try {
            Credentials credentials = Credentials.create(AdminPrivateKey);
            RewardPool contract = RewardPool.load(
                    rewardPool, web3j, credentials, new DefaultGasProvider());

            BigInteger balance = contract.getBalance(tokenAddress).send();
            return balance.toString();

        } catch (Exception e) {
            log.error("Balance query failed", e);
            return "0";
        }
    }
}
