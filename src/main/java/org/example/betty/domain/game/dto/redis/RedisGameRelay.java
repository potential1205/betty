package org.example.betty.domain.game.dto.redis;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameRelay {

    private String inning;                   // 현재 이닝 (예: 3회초)
    private String score;                    // 현재 점수 (예: LG 2 : 1 KT)
    private String situation;                // 아웃카운트 + 주자상황 (예: 2사 만루)
    private String ballCount;                // B-S (예: 2-1)
    private String result;                   // 현재 타석의 최종 결과 (예: 볼넷, 2루타 등)
    private List<String> playByPlay;         // 구 단위 텍스트 기록 (스트라이크, 파울, 헛스윙 등)
    private String teamAtBat;                // 공격 팀 (예: LG)
    private Integer batterOrder;             // 현재 타순 (1~9)
    private Boolean isFirstBatterOfInning;   // 이닝 시작 타자 여부

    private List<String> runnerOnBase;       // ["오스틴", "김상수", "장성우"] — 1루~3루 순

    private PlayerRelayInfo pitcher;         // 현재 투수 정보
    private PlayerRelayInfo batter;          // 현재 타자 정보
    private PlayerRelayInfo previousBatter;  // 직전 타자 정보
    private List<PlayerRelayInfo> nextBatters; // 대기 타자 3명
}

