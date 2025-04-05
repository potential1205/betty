package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameProblemServiceImpl implements GameProblemService {

    private final ProblemGenerator problemGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    // gameId → 이전 타자 이름 캐싱
    private final Map<String, String> previousBatterMap = new ConcurrentHashMap<>();

    @Override
    public void handleRelayUpdate(String gameId, RedisGameRelay relay) {
        PlayerRelayInfo batter = relay.getBatter();
        if (batter == null || batter.getName() == null) return;

        String nowBatter = batter.getName();
        String prevBatter = previousBatterMap.get(gameId);

        if (!nowBatter.equals(prevBatter)) {
            previousBatterMap.put(gameId, nowBatter);
            log.info("[타자 교체 감지] gameId={} | 교체: {} → {}", gameId, prevBatter, nowBatter);

            List<RedisGameProblem> problems = problemGenerator.generateCommonProblems(gameId, relay);
            for (RedisGameProblem problem : problems) {
                String key = "games:" + gameId + ":problems";
                redisTemplate.opsForHash().put(key, problem.getProblemId(), problem);
                log.info("[문제 생성] {} | 문제ID: {}", problem.getDescription(), problem.getProblemId());
            }
        }
    }
}