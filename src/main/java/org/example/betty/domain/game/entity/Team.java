package org.example.betty.domain.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String teamCode;

    @Column(length = 20, nullable = false)
    private String teamName;

    @Column(length = 3, nullable = false)
    private String tokenName;

    public Team(String teamCode, String teamName, String tokenName) {
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.tokenName = tokenName;
    }

}
