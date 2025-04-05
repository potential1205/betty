package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProblemGenerator {

    public List<RedisGameProblem> generateCommonProblems(String gameId, RedisGameRelay relay) {
        List<RedisGameProblem> result = new ArrayList<>();

        String batterName = relay.getBatter().getName();
        String inning = relay.getInning();

        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .batterName(batterName)
                .type("COMMON")
                .description("이번 타자가 출루할까요?")
                .options(List.of("O", "X"))
                .answer(null)
                .timestamp(System.currentTimeMillis())
                .build()
        );

        // TODO: 다른 문제도 계속 추가
        return result;
    }
}
