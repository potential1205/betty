package org.example.betty.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.game.dto.redis.live.RedisGameProblem;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameProblemDto {
    private RedisGameProblem redisGameProblem;
}
