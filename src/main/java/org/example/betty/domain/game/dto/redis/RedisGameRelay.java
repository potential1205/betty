package org.example.betty.domain.game.dto.redis;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameRelay {
    private String inning;  // 이닝 정보
    private String score;   // 점수
    private String batter;  // 타자 정보
    private String pitcher; // 투수 정보
    private String situation; // 상황 (예: 1사 만루)
    private List<String> playByPlay; // 투구 결과
}
