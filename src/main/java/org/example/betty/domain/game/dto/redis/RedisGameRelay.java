package org.example.betty.domain.game.dto.redis;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameRelay {

    private String inning;                   // 현재 이닝 (예: "5회말")
    private String teamAtBat;                // 공격 팀 (예: "삼성")
    private String score;                    // 현재 점수 (예: "LG 2 : KT 1")
    private int outCount;                    // 아웃 카운트 (예: 2)
    private List<String> pitchResult;        // 현재 타자의 투구 결과 (예: ["스트라이크", "볼", "파울"])
    private List<String> runnerOnBase;       // 각 루 주자 (예: ["1루 구자욱", "2루 강민호", "3루 김상수"])
    private PlayerRelayInfo pitcher;         // 현재 투수 정보
    private PlayerRelayInfo batter;          // 현재 타자 정보
    private PlayerRelayInfo previousBatter;  // 직전 타자 정보
    private List<String> nextBatters;        // 대기 타자 3명 (예: ["5번 김지찬", "6번 이원석", "7번 김현준"])
}
