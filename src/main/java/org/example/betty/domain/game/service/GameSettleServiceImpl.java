package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.exchange.service.SettlementService;
import org.example.betty.domain.game.dto.redis.live.RedisGameLiveResult;
import org.example.betty.domain.game.dto.redis.live.RedisGameProblem;
import org.example.betty.domain.game.entity.Team;
import org.example.betty.domain.game.repository.TeamRepository;
import org.example.betty.domain.reward.dto.RewardRequest;
import org.example.betty.domain.reward.service.RewardService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSettleServiceImpl implements GameSettleService {

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final TokenPriceRepository tokenPriceRepository;
    private final TokenRepository tokenRepository;
    private final TeamRepository teamRepository;
    private final RewardService rewardService;
    private final SettlementService settlementService;

    // LIVE 투표 정산
    @Override
    public void liveVoteSettle(Long gameId, Long homeTeamId, Long awayTeamId) {

        // 1BET 가치에 해당하는 팬 토큰 시세 조회
        Team homeTeam = teamRepository.findById(homeTeamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));
        Team awayTeam = teamRepository.findById(awayTeamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

        Token homeToken = tokenRepository.findByTokenName(homeTeam.getTokenName())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        Token awayToken = tokenRepository.findByTokenName(awayTeam.getTokenName())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        BigInteger homeTokenPrice = tokenPriceRepository.findByTokenName(homeTeam.getTokenName()).toBigInteger();
        BigInteger awayTokenPrice = tokenPriceRepository.findByTokenName(awayTeam.getTokenName()).toBigInteger();

        // 리워드를 보내 줄
        Map<String, Integer> homeMap = new HashMap<>();
        Map<String, Integer> awayMap = new HashMap<>();

        // 투표 결과 조회
        String problemKey = "livevote:result:" + gameId;
        List<RedisGameLiveResult> list = (List<RedisGameLiveResult>) redisTemplate2.opsForValue().get(problemKey);

        // 투표 결과에 따라 리워드 지급
        for (RedisGameLiveResult result : list) { // 투표 목록

            // 사용자 정보
            String walletAddress = result.getWalletAddress();

            // 문제, 정답지
            String voteResultKey = "livevote:" + gameId + ":" + result.getProblemId();
            RedisGameProblem problem = (RedisGameProblem) redisTemplate2.opsForValue().get(voteResultKey);

            if (problem.getAnswer().equals(result.getSelect())) {
                if (result.getMyTeamId().equals(homeTeamId)) {
                    if (homeMap.containsKey(walletAddress)) {
                        homeMap.put(walletAddress, homeMap.get(walletAddress) + 1);
                    } else {
                        homeMap.put(walletAddress, 1);
                    }
                } else {
                    if (awayMap.containsKey(walletAddress)) {
                        awayMap.put(walletAddress, awayMap.get(walletAddress) + 1);
                    } else {
                        awayMap.put(walletAddress, 1);
                    }
                }
            }
        }

        for (String walletAddress : homeMap.keySet()) {
            RewardRequest requet = new RewardRequest(homeToken.getTokenAddress(), walletAddress, homeTokenPrice.multiply(new BigInteger(String.valueOf(homeMap.get(walletAddress)))));
            rewardService.sendReward(requet);
        }

        for (String walletAddress : awayMap.keySet()) {
            RewardRequest requet = new RewardRequest(awayToken.getTokenAddress(), walletAddress, awayTokenPrice.multiply(new BigInteger(String.valueOf(awayMap.get(walletAddress)))));
            rewardService.sendReward(requet);
        }
    }

    // 사전 투표 정산
    @Override
    public void preVoteTeamSettle(Long gameId, Long winnerTeamId) {
        settlementService.finalizePreVoteTeamSettle(BigInteger.valueOf(gameId), BigInteger.valueOf(winnerTeamId));
        List<String> winnerWalletAddressList = settlementService.getWinningTeamBettors(BigInteger.valueOf(gameId));

        for (String winnerWalletAddress : winnerWalletAddressList) {
            settlementService.claimForUser(BigInteger.valueOf(gameId), winnerWalletAddress);
        }
    }

    @Override
    public void createPreVoteTeamSettle(Long gameId, Long teamAId, Long teamBId, Long startTime, String teamATokenAddress, String teamBTokenAddress) {
        settlementService.createPreVoteTeamSettle(
                BigInteger.valueOf(gameId),
                BigInteger.valueOf(teamAId),
                BigInteger.valueOf(teamBId),
                BigInteger.valueOf(startTime),
                teamATokenAddress,
                teamBTokenAddress
        );
    }
}
