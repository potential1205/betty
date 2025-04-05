package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProblemGenerator {

    public List<RedisGameProblem> generateCommonProblems(String gameId, RedisGameRelay relay) {
        List<RedisGameProblem> result = new ArrayList<>();

        PlayerRelayInfo batter = relay.getBatter();
        String batterName = batter.getName();
        String batterPosition = batter.getPosition(); // 예: "2번타자"
        String inning = relay.getInning();

        String description = String.format("%s %s가 출루할까요?", batterPosition, batterName);

        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .batterName(batterName)
                .type("COMMON")
                .description(description)
                .options(List.of("O", "X"))
                .answer(null)
                .timestamp(System.currentTimeMillis())
                .build()
        );

        return result;
    }
}
