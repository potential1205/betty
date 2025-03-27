package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameInfo {
    private int season;
    private String gameDate;
    private String startTime;
    private String stadium;
    private String homeTeam;
    private String awayTeam;
    private String status;
}
