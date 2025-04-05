package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameProblemServiceImpl implements GameProblemService {

    private final ProblemGenerator problemGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    // 이전 타자명 기록
    private final Map<String, RedisGameRelay> previousRelayMap = new ConcurrentHashMap<>();

    @Override
    public void handleRelayUpdate(String gameId, RedisGameRelay currentRelay) {
        PlayerRelayInfo currentBatter = currentRelay.getBatter();
        if (currentBatter == null || currentBatter.getName() == null) return;

        String redisKey = "games:" + LocalDate.now() + ":" + gameId;

//
//        RedisGameRelay previousRelay = (RedisGameRelay) redisTemplate.opsForHash().get(redisKey, "relay");
//
//        // 타자 이름 비교
//        String previousBatterName = previousRelay != null && previousRelay.getBatter() != null
//                ? previousRelay.getBatter().getName()
//                : null;
//
//        if (!currentBatter.getName().equals(previousBatterName)) {
//            System.out.println("[타자 교체 감지] gameId=" + gameId + " | " + previousBatterName + " → " + currentBatter.getName());
//            log.info("[타자 교체 감지] gameId={} | {} → {}", gameId, previousBatterName, currentBatter.getName());
//
//            // 문제 생성
//            List<RedisGameProblem> problems = problemGenerator.generateCommonProblems(gameId, currentRelay);
//            for (RedisGameProblem problem : problems) {
//                String problemKey = "games:" + gameId + ":problems";
//                redisTemplate.opsForHash().put(problemKey, problem.getProblemId(), problem);
//
//                System.out.println("[문제 생성] " + problem.getDescription() + " | 문제ID: " + problem.getProblemId());
//                log.info("[문제 생성] {} | 문제ID: {}", problem.getDescription(), problem.getProblemId());
//            }
//
//            // 현재 상태를 메모리에도 캐시 (선택)
//            previousRelayMap.put(gameId, currentRelay);
        }
    }

