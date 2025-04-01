package org.example.betty.domain.game.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameInfoResponse {

    private int season;
    private LocalDate gameDate;
    private LocalTime startTime;
    private String stadium;
    private String homeTeamName;
    private String awayTeamName;
    private String status;


}
