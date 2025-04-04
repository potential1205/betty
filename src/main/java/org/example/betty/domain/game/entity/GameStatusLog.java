package org.example.betty.domain.game.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_status_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Games game;

    @Column(nullable = false)
    private String status;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}

