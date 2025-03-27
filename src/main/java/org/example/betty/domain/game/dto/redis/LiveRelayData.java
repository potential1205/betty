package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRelayData {
    private String inning;
    private String score;
    private String batter;
    private String pitcher;
    private String situation;
    private List<String> playByPlay;
}
