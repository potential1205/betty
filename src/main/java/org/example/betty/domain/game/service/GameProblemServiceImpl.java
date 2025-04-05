package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameLineup;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
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
    private final RedisTemplate<String, Object> redisTemplate;

    // 이전 타자 이름 저장용 Map
    private final Map<String, String> previousBatterMap = new ConcurrentHashMap<>();

    /**
     * 중계 데이터가 업데이트될 때마다 호출됨
     * 타자 교체가 감지되면 문제 생성 후 이전 타자 갱신
     */
    @Override
    public void handleRelayUpdate(String gameId, RedisGameRelay currentRelay) {
        PlayerRelayInfo currentBatter = currentRelay.getBatter();
        if (currentBatter == null || currentBatter.getName() == null) return;

        String currentBatterName = currentBatter.getName();
        String previousBatterName = previousBatterMap.get(gameId);

        // 타자 이름이 바뀐 경우에만 문제 생성
        if (!currentBatterName.equals(previousBatterName)) {
            log.info("[타자 교체 감지] gameId={} | {} → {}", gameId, previousBatterName, currentBatterName);

            List<RedisGameProblem> problems = problemGenerator.generateCommonProblems(gameId, currentRelay);
            for (RedisGameProblem problem : problems) {
                String problemKey = "games:" + gameId + ":problems";
                redisTemplate.opsForHash().put(problemKey, problem.getProblemId(), problem);
                log.info("[문제 생성] {} | 문제ID: {}", problem.getDescription(), problem.getProblemId());
            }

            // 이전 타자 갱신
            previousBatterMap.put(gameId, currentBatterName);
        }
    }

    /**
     * 중계 시작 전에 호출: 원정팀 1번 타자 저장
     */
    public void initializeFirstBatterFromLineup(String gameId) {
        String redisKey = "games:" + LocalDate.now() + ":" + gameId;

        Object lineupObj = redisTemplate.opsForHash().get(redisKey, "lineup");

        if (!(lineupObj instanceof RedisGameLineup)) {
            log.warn("[라인업 없음] gameId={} | lineup 데이터가 존재하지 않거나 타입 불일치", gameId);
            return;
        }

        RedisGameLineup lineup = (RedisGameLineup) lineupObj;
        String firstBatterName = null;

        if (lineup.getAway() != null
                && lineup.getAway().getStarterBatters() != null
                && !lineup.getAway().getStarterBatters().isEmpty()
                && lineup.getAway().getStarterBatters().get(0) != null) {

            firstBatterName = lineup.getAway().getStarterBatters().get(0).getName();
        }

        if (firstBatterName != null) {
            previousBatterMap.put(gameId, firstBatterName);
            log.info("[초기 타자 저장] gameId={} | 원정팀 1번 타자: {}", gameId, firstBatterName);
        } else {
            log.warn("[1번 타자 없음] gameId={} | away starterBatters 리스트가 비었거나 null", gameId);
        }
    }
}
