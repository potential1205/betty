package org.example.betty.domain.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String teamCode;

    @Column(length = 20, nullable = false)
    private String teamName;

    @Column(length = 3, nullable = false)
    private String tokenCode;
}
