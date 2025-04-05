package org.example.betty.domain.game.dto.redis;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class RedisGameProblem {

    private final String problemId;      // 문제 고유 ID (UUID)
    private final String gameId;         // 어떤 경기의 문제인지
    private final String inning;         // 이닝 정보 (ex: 3회말)
    private final String batterName;     // 문제 타자
    private final String type;           // 문제 유형 (ex: COMMON, SPECIAL)
    private final String description;    // 문제 내용
    private final List<String> options;  // 보기 (예: ["O", "X"] or ["1~3구", "4~6구", "7구 이상"])
    private final String answer;         // 정답 (null이면 아직 결정되지 않음)
    private final long timestamp;        // 문제 생성 시각 (System.currentTimeMillis())
}
