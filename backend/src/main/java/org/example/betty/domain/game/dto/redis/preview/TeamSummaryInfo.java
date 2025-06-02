package org.example.betty.domain.game.dto.redis.preview;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamSummaryInfo {
    private String teamName;     // 예: "한화"
    private int rank;            // 예: 10
    private int wins;            // 예: 5
    private int draws;           // 예: 0
    private int losses;          // 예: 10
}