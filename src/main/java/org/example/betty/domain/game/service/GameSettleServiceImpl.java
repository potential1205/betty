package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.entity.Token;
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
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSettleServiceImpl implements GameSettleService {

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final TokenRepository tokenRepository;
    private final TeamRepository teamRepository;
    private final RewardService rewardService;
    private final SettlementService settlementService;

    // LIVE 투표 정산
    @Override
    public void liveVoteSettle(Long gameId) {
        Map<String, Integer> correctCounts = new HashMap<>();

        // 투표 결과를 Redis에서 조회
        String resultKey = "livevote:result:" + gameId;
        List<RedisGameLiveResult> results = (List<RedisGameLiveResult>) redisTemplate2.opsForValue().get(resultKey);

        if (results == null || results.isEmpty()) {
            log.info("[LIVE VOTE SETTLE] No vote results found for gameId={}", gameId);
            return;
        }

        for (RedisGameLiveResult result : results) {
            String walletAddress = result.getWalletAddress();
            String voteResultKey = "livevote:" + gameId + ":" + result.getProblemId();
            RedisGameProblem problem = (RedisGameProblem) redisTemplate2.opsForValue().get(voteResultKey);

            if (problem != null && problem.getAnswer().equals(result.getSelect())) {
                correctCounts.merge(walletAddress, 1, Integer::sum);
            }
        }

        for (Map.Entry<String, Integer> entry : correctCounts.entrySet()) {
            String walletAddress = entry.getKey();
            int correctCount = entry.getValue();

            long randomTokenType = ThreadLocalRandom.current().nextLong(1, 11);

            Team team = teamRepository.findById(randomTokenType)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

            Token token = tokenRepository.findByTokenName(team.getTokenName())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM));

            RewardRequest rewardRequest = new RewardRequest(
                    token.getTokenAddress(),
                    walletAddress,
                    BigInteger.valueOf(correctCount)
            );
            rewardService.sendReward(rewardRequest);
            log.info("[REWARD SENT] walletAddress={}, tokenType={}, tokenAddress={}, correctCount={}",
                    walletAddress, randomTokenType, token.getTokenAddress(), correctCount);
        }

        log.info("[LIVE VOTE SETTLE COMPLETED] gameId={}", gameId);
    }

    // 팀 사전 투표 생성
    @Override
    public void createPreVoteTeamSettle(Long gameId, Long teamAId, Long teamBId, Long startTime, String teamATokenAddress, String teamBTokenAddress) {
        settlementService.createGame(
                BigInteger.valueOf(gameId),
                BigInteger.valueOf(teamAId),
                BigInteger.valueOf(teamBId),
                BigInteger.valueOf(startTime),
                teamATokenAddress,
                teamBTokenAddress
        );
    }

    // 팀 사전 투표 정산
    @Override
    public void preVoteTeamSettle(Long gameId, Long winnerTeamId) {
        settlementService.finalize(BigInteger.valueOf(gameId), BigInteger.valueOf(winnerTeamId));
        List<String> winnerWalletAddressList = settlementService.getWinningTeamBettors(BigInteger.valueOf(gameId));

        for (String winnerWalletAddress : winnerWalletAddressList) {
            settlementService.claimForUser(BigInteger.valueOf(gameId), winnerWalletAddress);
        }
    }

    // MVP 사전 투표 생성
    @Override
    public void createPreVoteMVPSettle(Long gameId, List<Long> playerIds, List<String> tokenAddresses, Long startTime) {
        settlementService.createMVPGame(
                BigInteger.valueOf(gameId),
                playerIds.stream().map(BigInteger::valueOf).toList(),
                tokenAddresses,
                BigInteger.valueOf(startTime)
        );
    }

    // MVP 사전 투표 정산
    @Override
    public void preVoteMVPSettle(Long gameId, Long winningPlayerId) {
        settlementService.finalizePreVoteMVP(BigInteger.valueOf(gameId), BigInteger.valueOf(winningPlayerId));
        List<String> winnerWalletAddressList = settlementService.getWinningVoters(BigInteger.valueOf(gameId));

        for (String winnerWalletAddress : winnerWalletAddressList) {
            settlementService.claimMVPRewardForUser(BigInteger.valueOf(gameId), winnerWalletAddress);
        }
    }
}
