package org.example.betty.domain.match.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.match.Game;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    private Long id;
    private LocalDateTime matchStartDateTime;

    public static GameResponse of(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .matchStartDateTime(game.getMatchStartDateTime())
                .build();
    }
}
