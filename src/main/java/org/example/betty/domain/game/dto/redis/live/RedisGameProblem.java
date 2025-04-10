package org.example.betty.domain.game.dto.redis.live;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisGameProblem implements Serializable {
    private String problemId;
    private String gameId;
    private String inning;
    private String attackTeam;
    private String batterName;
    private String questionCode;
    private String description;
    private List<String> options;
    private String answer;

    @JsonProperty("push")
    private boolean push;
    private String timestamp;
}
