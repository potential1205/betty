package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisGameData {
    private GameBasicInfo gameInfo;
    private TeamLineup lineup;
    private LiveRelayData relay;
}
