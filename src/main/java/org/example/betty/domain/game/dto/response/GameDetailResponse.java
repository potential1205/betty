package org.example.betty.domain.game.dto.response;

import lombok.*;
import org.example.betty.domain.game.entity.Game;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameDetailResponse {
    private Game game;
}
