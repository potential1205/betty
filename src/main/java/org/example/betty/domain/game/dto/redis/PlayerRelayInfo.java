package org.example.betty.domain.game.dto.redis;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRelayInfo {
    private String name;           // 선수명
    private String position;       // 예: "3번타자(좌타)", "투수(우투)"

    // 타자 기록
    private Double avg;            // 타율

    // 투수 기록
    private Integer inningPitched; // 이닝
    private Integer totalPitches;  // 투구수

    private String summaryText;    // 공격 결과 요약 (예: "2루수 병살타 아웃" 같은 결과)
}
