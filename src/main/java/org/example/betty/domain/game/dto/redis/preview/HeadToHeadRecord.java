package org.example.betty.domain.game.dto.redis.preview;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeadToHeadRecord {
    private String teamName;  // 예: "한화"
    private int wins;         // 예: 1
    private int draws;        // 예: 0
    private int losses;       // 예: 1
}