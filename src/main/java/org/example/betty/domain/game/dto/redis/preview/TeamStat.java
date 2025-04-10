package org.example.betty.domain.game.dto.redis.preview;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamStat {
    private String teamName;   // 예: "한화"
    private double winRate;    // 예: 0.333
    private double avg;        // 타율 (예: 0.186)
    private double era;        // 평균자책점 (예: 4.53)
}
