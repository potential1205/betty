package org.example.betty.domain.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor
public class Games {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Teams homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Teams awayTeam;

    @Column(length = 20)
    private String stadium;

    @Column(nullable = false)
    private int season;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(length = 20, nullable = false)
    private String status;

    public Games(Teams homeTeam, Teams awayTeam, String stadium, int season, LocalDate gameDate, LocalTime startTime, String status) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.stadium = stadium;
        this.season = season;
        this.gameDate = gameDate;
        this.startTime = startTime;
        this.status = status;
    }

}

