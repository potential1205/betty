package org.example.betty.domain.game.dto.response;

import lombok.*;
import org.example.betty.domain.game.dto.redis.preview.TeamComparisonDto;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameInfoResponse {
    private Long gameId;
    private Long homeTeamId;
    private Long awayTeamId;
    private int season;
    private String gameDate;
    private String startTime;
    private String stadium;
    private String homeTeamName;
    private String awayTeamName;
    private String status;
    private TeamComparisonDto teamComparison;
}
