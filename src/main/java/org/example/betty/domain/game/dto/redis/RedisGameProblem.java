package org.example.betty.domain.game.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
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
    private String batterName;
    private String type;
    private String description;
    private List<String> options;
    private String answer;
    private long timestamp;

    // redis 저장x
    @JsonIgnore
    public String getFormattedTimestamp() {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
