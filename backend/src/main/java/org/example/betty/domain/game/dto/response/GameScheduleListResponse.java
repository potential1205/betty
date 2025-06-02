package org.example.betty.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.game.dto.redis.RedisGameSchedule;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GameScheduleListResponse {
    private List<RedisGameSchedule> schedules;
}
