package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameProblemServiceImpl implements GameProblemService {

    private final ProblemGenerator problemGenerator;

    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;

    private final Map<String, String> previousBatterMap = new ConcurrentHashMap<>();

    @Override
    public void handleRelayUpdate(String gameId, RedisGameRelay currentRelay) {
        PlayerRelayInfo currentBatter = currentRelay.getBatter();
        if (currentBatter == null || currentBatter.getName() == null) return;

        String currentBatterName = currentBatter.getName();
        String previousBatterName = previousBatterMap.get(gameId);

        // 최초 실행: 초기화만 하고 문제 출제 생략
        if (previousBatterName == null) {
            previousBatterMap.put(gameId, currentBatterName);
            log.info("[초기 중계 세팅] gameId={} | 현재 타자로 초기화: {}", gameId, currentBatterName);
            return;
        }

        // 타자 교체 감지
        if (!currentBatterName.equals(previousBatterName)) {

            // 1. 정답 채점
            PlayerRelayInfo prevBatter = currentRelay.getPreviousBatter();
            if (prevBatter != null && prevBatter.getName() != null) {
                String answer = determineAnswer(prevBatter);
                updateProblemAnswer(gameId, prevBatter.getName(), answer);
            }

            // 2. 새 문제 출제
            List<RedisGameProblem> problems = problemGenerator.generateCommonProblems(gameId, currentRelay);
            String redisKey = "games:" + LocalDate.now() + ":" + gameId + ":problems";
            ListOperations<String, Object> listOps = redisTemplate2.opsForList();

            for (RedisGameProblem problem : problems) {
                listOps.rightPush(redisKey, problem);
                log.info("[문제 생성] {} | 문제ID: {}", problem.getDescription(), problem.getProblemId());
            }

            // 3. 타자 갱신
            previousBatterMap.put(gameId, currentBatterName);
        }
    }

    private String determineAnswer(PlayerRelayInfo prevBatter) {
        String summary = prevBatter.getSummaryText();
        if (summary == null) return null;

        if (summary.contains("안타") || summary.contains("볼넷") || summary.contains("사구") || summary.contains("실책")) {
            return "O";
        }
        return "X";
    }

    private void updateProblemAnswer(String gameId, String batterName, String answer) {
        String redisKey = "games:" + LocalDate.now() + ":" + gameId + ":problems";
        ListOperations<String, Object> listOps = redisTemplate2.opsForList();
        List<Object> problems = listOps.range(redisKey, 0, -1);

        for (int i = 0; i < problems.size(); i++) {
            Object obj = problems.get(i);
            if (obj instanceof RedisGameProblem problem &&
                    problem.getAnswer() == null &&
                    problem.getBatterName().equals(batterName)) {

                RedisGameProblem updated = RedisGameProblem.builder()
                        .problemId(problem.getProblemId())
                        .gameId(problem.getGameId())
                        .inning(problem.getInning())
                        .batterName(problem.getBatterName())
                        .type(problem.getType())
                        .description(problem.getDescription())
                        .options(problem.getOptions())
                        .answer(answer)
                        .timestamp(problem.getTimestamp())
                        .build();

                listOps.set(redisKey, i, updated);
                log.info("[문제 정답 확정] 문제ID: {} | 타자: {} | 정답: {}", problem.getProblemId(), batterName, answer);
                return;
            }
        }

        log.warn("[정답 업데이트 실패] gameId={} | 타자: {} | 문제를 찾을 수 없음", gameId, batterName);
    }
}
