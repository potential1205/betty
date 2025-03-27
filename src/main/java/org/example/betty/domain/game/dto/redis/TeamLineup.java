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
public class TeamLineup {
    private List<String> home;
    private List<String> away;
}
