package org.example.betty.domain.game.dto.redis.live;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisGameLiveResult {
    private String walletAddress;
    private String select;
    private String problemId;
}
