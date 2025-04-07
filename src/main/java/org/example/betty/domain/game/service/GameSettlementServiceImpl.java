package org.example.betty.domain.game.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.betty.domain.game.dto.redis.PreVoteAnswer;
import org.example.betty.domain.game.dto.redis.PreVoteMyPick;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameSettlementServiceImpl implements GameSettlementService {

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    public void preVoteSettle(Long gameId) {
        String key = "prevote:result" + ":" + gameId;
        PreVoteAnswer preVoteAnswer = (PreVoteAnswer) redisTemplate2.opsForValue().get(key);

        settleTeamVote(gameId, preVoteAnswer.getWinner());
        settleMvpVote(gameId, preVoteAnswer.getMvp(), preVoteAnswer.getMvpTeam());
    }

    private void settleMvpVote(Long gameId, String mvp, String mvpTeam) {
        String key = "prevote:mvp:" + gameId;
        Map<Object, Object> rawVoteMap = redisTemplate2.opsForHash().entries(key);

        BigDecimal totalWinningBet = BigDecimal.ZERO;
        BigDecimal totalLosingBet_A = BigDecimal.ZERO;
        BigDecimal totalLosingBet_B = BigDecimal.ZERO;

        List<VoteRecord> winningVotes = new ArrayList<>();
        List<VoteRecord> losingVotes = new ArrayList<>();

        // 각 투표 데이터를 순회하면서 승리자와 패배자 분류
        for (Map.Entry<Object, Object> entry : rawVoteMap.entrySet()) {
            String walletAddress = (String) entry.getKey();
            PreVoteMyPick pick = (PreVoteMyPick) entry.getValue();

            if (mvp.equals(pick.getMvpPick())) {
                winningVotes.add(new VoteRecord(walletAddress, pick));
                totalWinningBet = totalWinningBet.add(pick.getMvpPickBetAmount());
            } else {
                losingVotes.add(new VoteRecord(walletAddress, pick));
                if (isATeamCandidate(pick.getMvpPick())) {
                    totalLosingBet_A = totalLosingBet_A.add(pick.getMvpPickBetAmount());
                } else {
                    totalLosingBet_B = totalLosingBet_B.add(pick.getMvpPickBetAmount());
                }
            }
        }

        // 각 토큰 종류별 보너스 풀 계산: 패배자 베팅액의 50%
        BigDecimal evenPool_A = totalLosingBet_A.multiply(new BigDecimal("0.5"));
        BigDecimal proportionalPool_A = totalLosingBet_A.multiply(new BigDecimal("0.5"));
        BigDecimal evenPool_B = totalLosingBet_B.multiply(new BigDecimal("0.5"));
        BigDecimal proportionalPool_B = totalLosingBet_B.multiply(new BigDecimal("0.5"));

        int winnerCount = winningVotes.size();
        BigDecimal evenShare_A = (winnerCount > 0)
                ? evenPool_A.divide(new BigDecimal(winnerCount), 10, RoundingMode.DOWN)
                : BigDecimal.ZERO;
        BigDecimal evenShare_B = (winnerCount > 0)
                ? evenPool_B.divide(new BigDecimal(winnerCount), 10, RoundingMode.DOWN)
                : BigDecimal.ZERO;

        boolean winningIsATeam = mvpTeam.equalsIgnoreCase("A");

        // 각 승리자별로 보너스 산출: 승리자의 베팅액 비율에 따른 비율 분배 몫 계산
        for (VoteRecord vote : winningVotes) {
            PreVoteMyPick pick = vote.getPick();
            BigDecimal betAmount = pick.getMvpPickBetAmount(); // 승리자가 베팅한 원금 (예, A팀 토큰이면 A팀 토큰)

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

            // 최종 정산:
            // 승리자가 올바른 후보가 A팀 소속이면,
            //   - A팀 토큰: 원금(본인 베팅금) + bonusA
            //   - B팀 토큰: bonusB
            // 반대로 승리 후보가 B팀 소속이면,
            //   - B팀 토큰: 원금 + bonusB
            //   - A팀 토큰: bonusA
            if (winningIsATeam) {
                System.out.println("Wallet " + vote.getWalletAddress() + " receives:");
                System.out.println("  A-team tokens (returned bet + bonus): " + betAmount.add(bonusA));
                System.out.println("  B-team tokens bonus: " + bonusB);
            } else {
                System.out.println("Wallet " + vote.getWalletAddress() + " receives:");
                System.out.println("  B-team tokens (returned bet + bonus): " + betAmount.add(bonusB));
                System.out.println("  A-team tokens bonus: " + bonusA);
            }
        }
    }

    // 헬퍼 메서드: 후보가 A팀 소속인지 여부 판별 (구현은 상황에 맞게)
    private boolean isATeamCandidate(String candidate) {
        // 예를 들어, "a", "b", "c"가 A팀, "e", "f", "g"가 B팀이라고 가정
        return candidate.equalsIgnoreCase("a")
                || candidate.equalsIgnoreCase("b")
                || candidate.equalsIgnoreCase("c");
    }


    private void settleTeamVote(Long gameId, String winningTeam) {
        String key = "prevote" + ":" + "team" + ":" + gameId;
        Map<Object, Object> rawVoteMap = redisTemplate2.opsForHash().entries(key);

        // 1. MVP 투표 결과 정산을 위해 총 베팅액을 계산
        BigDecimal totalWinningBet = BigDecimal.ZERO;
        BigDecimal totalLosingBet = BigDecimal.ZERO;

        // 2. 투표 데이터를 올바른 그룹으로 분류
        List<VoteRecord> winningVotes = new ArrayList<>();
        List<VoteRecord> losingVotes = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : rawVoteMap.entrySet()) {
            String walletAddress = (String) entry.getKey();
            PreVoteMyPick pick = (PreVoteMyPick) entry.getValue();

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

        // 균등 분배 몫: 모든 승리자에게 동일하게 분배
        BigDecimal evenShare = (winnerCount > 0)
                ? evenPool.divide(new BigDecimal(winnerCount), 10, RoundingMode.DOWN)
                : BigDecimal.ZERO;

        for (VoteRecord vote : winningVotes) {
            PreVoteMyPick pick = vote.getPick();
            BigDecimal betAmount = pick.getTeamPickBetAmount();

            // 비율 분배 몫: 승리자의 베팅 금액 비율에 따라 분배
            BigDecimal proportionalShare = BigDecimal.ZERO;
            if (totalWinningBet.compareTo(BigDecimal.ZERO) > 0) {
                proportionalShare = betAmount.divide(totalWinningBet, 10, RoundingMode.DOWN)
                        .multiply(proportionalPool);
            }

            // 최종 정산 금액: 균등분배 + 비율분배
            BigDecimal settlement = evenShare.add(proportionalShare);

            // 여기서 스마트 컨트랙트나 기타 로직을 통해 walletAddress에게 패배팀의 토큰을 settlement 만큼 지급
            // + 자신이 베팅한 금액을 walletAddress에게 승리팀의 토큰을 배팅한걸 돌려주기
            //  settlementService.transfer(vote.getWalletAddress(), settlement); -> 패배 팀 토큰
            //  settlementService.transfer(vote.getWalletAddress(), pick.getTeamPickBetAmount()); -> 승리 팀 토큰
            System.out.println("Wallet " + vote.getWalletAddress() + " receives: " + settlement);
        }
    }

    @Getter
    @AllArgsConstructor
    private class VoteRecord {
        private String walletAddress;
        private PreVoteMyPick pick;
    }
}
