package org.example.betty.domain.game.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    private Long id;
    private LocalDateTime gameStartDateTime;

//    public static GameResponse of(Game game) {
//        return GameResponse.builder()
//                .id(game.getId())
//                .gameStartDateTime(game.getGameStartDateTime())
//                .build();
//    }
}
