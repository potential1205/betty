package org.example.betty.domain.game.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
import org.example.betty.domain.game.dto.redis.PrePick;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSettlementServiceImpl implements GameSettlementService {

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final TokenPriceRepository tokenPriceRepository;
    private final TokenRepository tokenRepository;
    private final TeamRepository teamRepository;
    private final RewardService rewardService;

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

    public void preVoteSettle(Long gameId) {
        String resultKey = "prevote:result:" + gameId;
        PreVoteAnswer preVoteAnswer = (PreVoteAnswer) redisTemplate2.opsForValue().get(resultKey);

        if (preVoteAnswer == null) {
            log.warn("No pre-vote answer found for gameId: {}", gameId);
            return;
        }

        settleTeamBets(gameId, preVoteAnswer.getWinnerTeam());
        settleMvpBets(
                gameId,
                preVoteAnswer.getMvpName(),
                preVoteAnswer.getMvpTeam(),
                preVoteAnswer.getTeamA(),
                preVoteAnswer.getTeamB()
        );
    }

    private void settleMvpBets(Long gameId, String predictedMvpName, String predictedMvpTeam, String actualTeamA, String actualTeamB) {
        String key = "prevote:mvp:" + gameId;
        Map<Object, Object> rawVoteMap = redisTemplate2.opsForHash().entries(key);

        BigDecimal totalWinningBet = BigDecimal.ZERO;
        BigDecimal totalLosingBet_A = BigDecimal.ZERO;
        BigDecimal totalLosingBet_B = BigDecimal.ZERO;

        List<VoteRecord> winningVotes = new ArrayList<>();
        List<VoteRecord> losingVotes = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : rawVoteMap.entrySet()) {
            String walletAddress = (String) entry.getKey();
            PrePick pick = (PrePick) entry.getValue();

            if (predictedMvpName.equals(pick.getMvpPick())) {
                winningVotes.add(new VoteRecord(walletAddress, pick));
                totalWinningBet = totalWinningBet.add(pick.getMvpPickBetAmount());
            } else {
                losingVotes.add(new VoteRecord(walletAddress, pick));
                if (predictedMvpTeam.equals(pick.getMvpTeam())) {
                    totalLosingBet_A = totalLosingBet_A.add(pick.getMvpPickBetAmount());
                } else {
                    totalLosingBet_B = totalLosingBet_B.add(pick.getMvpPickBetAmount());
                }
            }
        }

        BigDecimal evenPool_A = totalLosingBet_A.multiply(new BigDecimal("0.5"));
        BigDecimal proportionalPool_A = totalLosingBet_A.multiply(new BigDecimal("0.5"));
        BigDecimal evenPool_B = totalLosingBet_B.multiply(new BigDecimal("0.5"));
        BigDecimal proportionalPool_B = totalLosingBet_B.multiply(new BigDecimal("0.5"));

        int winnerCount = winningVotes.size();
        BigDecimal evenShare_A = (winnerCount > 0) ? evenPool_A.divide(BigDecimal.valueOf(winnerCount), 10, RoundingMode.DOWN) : BigDecimal.ZERO;
        BigDecimal evenShare_B = (winnerCount > 0) ? evenPool_B.divide(BigDecimal.valueOf(winnerCount), 10, RoundingMode.DOWN) : BigDecimal.ZERO;

        for (VoteRecord vote : winningVotes) {
            PrePick pick = vote.getPick();
            BigDecimal betAmount = pick.getMvpPickBetAmount();

            BigDecimal proportionalShare_A = BigDecimal.ZERO;
            BigDecimal proportionalShare_B = BigDecimal.ZERO;

            if (totalWinningBet.compareTo(BigDecimal.ZERO) > 0) {
                proportionalShare_A = betAmount.divide(totalWinningBet, 10, RoundingMode.DOWN)
                        .multiply(proportionalPool_A);
                proportionalShare_B = betAmount.divide(totalWinningBet, 10, RoundingMode.DOWN)
                        .multiply(proportionalPool_B);
            }

            BigDecimal bonusA = evenShare_A.add(proportionalShare_A);
            BigDecimal bonusB = evenShare_B.add(proportionalShare_B);

            // 실제 전송 로직은 settlementService.transfer(...)로 대체 가능
            log.info("Wallet {} - 전리품 A: {}, 전리품 B: {}, 회수: {} (토큰 종류: {})",
                    vote.getWalletAddress(), bonusA, bonusB, betAmount,
                    predictedMvpTeam.equals(actualTeamA) ? "A 토큰" : "B 토큰");
        }
    }

    private void settleTeamBets(Long gameId, String winningTeam) {
        String key = "prevote:team:" + gameId;
        Map<Object, Object> rawVoteMap = redisTemplate2.opsForHash().entries(key);

        BigDecimal totalWinningBet = BigDecimal.ZERO;
        BigDecimal totalLosingBet = BigDecimal.ZERO;

        List<VoteRecord> winningVotes = new ArrayList<>();
        List<VoteRecord> losingVotes = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : rawVoteMap.entrySet()) {
            String walletAddress = (String) entry.getKey();
            PrePick pick = (PrePick) entry.getValue();

            if (winningTeam.equals(pick.getTeamPick())) {
                winningVotes.add(new VoteRecord(walletAddress, pick));
                totalWinningBet = totalWinningBet.add(pick.getTeamPickBetAmount());
            } else {
                losingVotes.add(new VoteRecord(walletAddress, pick));
                totalLosingBet = totalLosingBet.add(pick.getTeamPickBetAmount());
            }
        }

        BigDecimal evenPool = totalLosingBet.multiply(new BigDecimal("0.5"));
        BigDecimal proportionalPool = totalLosingBet.multiply(new BigDecimal("0.5"));
        int winnerCount = winningVotes.size();

        BigDecimal evenShare = (winnerCount > 0)
                ? evenPool.divide(BigDecimal.valueOf(winnerCount), 10, RoundingMode.DOWN)
                : BigDecimal.ZERO;

        for (VoteRecord vote : winningVotes) {
            PrePick pick = vote.getPick();
            BigDecimal betAmount = pick.getTeamPickBetAmount();

            BigDecimal proportionalShare = BigDecimal.ZERO;
            if (totalWinningBet.compareTo(BigDecimal.ZERO) > 0) {
                proportionalShare = betAmount.divide(totalWinningBet, 10, RoundingMode.DOWN)
                        .multiply(proportionalPool);
            }

            BigDecimal settlement = evenShare.add(proportionalShare);

            log.info("Wallet {} - 전리품: {}, 회수: {}",
                    vote.getWalletAddress(), settlement, betAmount);
        }
    }

    @Getter
    private static class VoteRecord {
        private final String walletAddress;
        private final PrePick pick;

        public VoteRecord(String walletAddress, PrePick pick) {
            this.walletAddress = walletAddress;
            this.pick = pick;
        }
    }
}
