package org.example.betty.domain.game.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamLineup {
    private PlayerInfo starterPitcher;
    private List<PlayerInfo> starterBatters;
}

