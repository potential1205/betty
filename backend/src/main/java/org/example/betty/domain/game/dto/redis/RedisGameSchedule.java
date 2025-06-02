package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameSchedule {
    private int season;   // 2025
    private String gameDate; // "2025-03-27"
    private String startTime; // "18:30"
    private String stadium;   // "잠실"
    private String homeTeam;  // "LG 트윈스"
    private String awayTeam;  // "한화 이글스"
    private String status;    // "SCHEDULED", "LIVE" 등
}
