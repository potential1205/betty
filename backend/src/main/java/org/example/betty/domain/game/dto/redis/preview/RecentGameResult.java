package org.example.betty.domain.game.dto.redis.preview;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentGameResult {
    private String teamName;       // 예: "한화"
    private List<String> results;  // 예: ["패", "승", "패", "패", "승"]
}
