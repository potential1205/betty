package org.example.betty.external.game.scraper.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameSchedule {
    private String season;
    private String gameDate;
    private String startTime;
    private String stadium;
    private String awayTeam;
    private String homeTeam;
    private String status;
}

