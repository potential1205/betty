package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.QuestionCode;
import org.example.betty.domain.game.dto.redis.live.RedisGameProblem;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ProblemGenerator {

    public List<RedisGameProblem> generateAllProblems(String gameId, RedisGameRelay relay) {
        List<RedisGameProblem> result = new ArrayList<>();
        PlayerRelayInfo batter = relay.getBatter();
        PlayerRelayInfo pitcher = relay.getPitcher();
        String now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        if (batter == null || pitcher == null) return result;

        String batterName = batter.getName();
        String batterPosition = batter.getPosition();  // 예: "3번 타자"
        String pitcherName = pitcher.getName();
        String inning = relay.getInning();
        String attackTeam = relay.getTeamAtBat();

        // 1. 공통 문제
        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .attackTeam(attackTeam)
                .batterName(batterName)
                .questionCode(QuestionCode.PITCH_COUNT.name())
                .description(String.format("%s %s의 타석은 몇 구 안에 승부가 날까요?", batterPosition, batterName))
                .options(List.of("① 1~4구", "② 5~8구", "③ 9구 이상"))
                .timestamp(now)
                .build());

        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .attackTeam(attackTeam)
                .batterName(batterName)
                .questionCode(QuestionCode.ON_BASE.name())
                .description(String.format("%s %s는 출루할 수 있을까요?", batterPosition, batterName))
                .options(List.of("O", "X"))
                .timestamp(now)
                .build());

        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .attackTeam(attackTeam)
                .batterName(batterName)
                .questionCode(QuestionCode.STRIKE_OUT.name())
                .description(String.format("투수 %s가 %s %s을 삼진으로 잡을 수 있을까요?", pitcherName, batterPosition, batterName))
                .options(List.of("O", "X"))
                .timestamp(now)
                .build());

        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .attackTeam(attackTeam)
                .batterName(batterName)
                .questionCode(QuestionCode.STRIKE_SEQUENCE.name())
                .description(String.format("투수 %s, 이번 타석 초반 2연속 스트라이크로 기세를 가져올 수 있을까요?", pitcherName))
                .options(List.of("O", "X"))
                .timestamp(now)
                .build());


        result.add(RedisGameProblem.builder()
                .problemId(UUID.randomUUID().toString())
                .gameId(gameId)
                .inning(inning)
                .attackTeam(attackTeam)
                .batterName(batterName)
                .questionCode(QuestionCode.HOME_RUN.name())
                .description(String.format("%s %s는 이번 타석에서 홈런을 칠 수 있을까요?", batterPosition, batterName))
                .options(List.of("O", "X"))
                .timestamp(now)
                .build());

        // 2. 특수 문제 조건 체크 및 생성
        // 선발 투수 이닝 책임은 1이닝에서만
        if (inning.startsWith("1")) {
            result.add(RedisGameProblem.builder()
                    .problemId(UUID.randomUUID().toString())
                    .gameId(gameId)
                    .inning(inning)
                    .attackTeam(attackTeam)
                    .batterName(pitcherName)
                    .questionCode(QuestionCode.PITCHER_DUTY.name())
                    .description("선발 투수는 몇 이닝까지 책임질까요?")
                    .options(List.of("① 5이닝 이하", "② 6~7이닝", "③ 8이닝 이상"))
                    .timestamp(now)
                    .build());
        }

        // 2루 주자가 있는 경우에만 타점 문제 생성
        if (relay.getRunnerOnBase() != null && relay.getRunnerOnBase().stream().anyMatch(r -> r.contains("2루"))) {
            result.add(RedisGameProblem.builder()
                    .problemId(UUID.randomUUID().toString())
                    .gameId(gameId)
                    .inning(inning)
                    .attackTeam(attackTeam)
                    .batterName(batterName)
                    .questionCode(QuestionCode.RECORD_RBI.name())
                    .description(String.format("%s %s는 타점을 기록할 수 있을까요?", batterPosition, batterName))
                    .options(List.of("O", "X"))
                    .timestamp(now)
                    .build());
        }

        // 이닝 시작 시점에만 첫 출루 문제 생성
        if ("0".equals(String.valueOf(relay.getOutCount()))) {
            result.add(RedisGameProblem.builder()
                    .problemId(UUID.randomUUID().toString())
                    .gameId(gameId)
                    .inning(inning)
                    .attackTeam(attackTeam)
                    .batterName(batterName)
                    .questionCode(QuestionCode.FIRST_ON_BASE.name())
                    .description(String.format("%s %s는 이번 이닝의 첫 출루자가 될 수 있을까요?", batterPosition, batterName))
                    .options(List.of("O", "X"))
                    .timestamp(now)
                    .build());
        }

        // 무실점 이닝 문제는 이닝 시작 시점에만 생성
        if ("0".equals(String.valueOf(relay.getOutCount()))) {
            result.add(RedisGameProblem.builder()
                    .problemId(UUID.randomUUID().toString())
                    .gameId(gameId)
                    .inning(inning)
                    .attackTeam(attackTeam)
                    .batterName(pitcherName)
                    .questionCode(QuestionCode.NO_RUN_ALLOWED.name())
                    .description(String.format("투수 %s는 이번 이닝을 실점 없이 끝낼 수 있을까요?", pitcherName))
                    .options(List.of("O", "X"))
                    .timestamp(now)
                    .build());
        }

        // (9이닝 시작 시점 동점인 상황) 9회말까지 경기가 이어질까요?
        if ("9회초".equals(inning) && isTiedScore(relay.getScore())) {
            result.add(RedisGameProblem.builder()
                    .problemId(UUID.randomUUID().toString())
                    .gameId(gameId)
                    .inning("9회초")
                    .attackTeam(attackTeam)
                    .batterName(null)
                    .questionCode(QuestionCode.GO_TO_BOTTOM_9 .name()) // 새로운 질문 코드 사용하는 걸 추천
                    .description("경기가 9회말까지 이어질까요?")
                    .options(List.of("O", "X"))
                    .timestamp(now)
                    .build());
        }


        return result;
    }

    private boolean isTiedScore(String score) {
        try {
            String[] parts = score.split(":");
            if (parts.length != 2) return false;
            int left = Integer.parseInt(parts[0].replaceAll("[^0-9]", "").trim());
            int right = Integer.parseInt(parts[1].replaceAll("[^0-9]", "").trim());
            return left == right;
        } catch (Exception e) {
            return false;
        }
    }
}
