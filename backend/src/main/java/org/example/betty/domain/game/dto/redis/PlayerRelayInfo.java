package org.example.betty.domain.game.dto.redis;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRelayInfo {
    private String name;           // 선수명 (ex: 구자욱)
    private String position;       // 포지션/타순 정보 (ex: 2번타자(좌타), 투수(우투))
    private String avg;            // (현재타자만) 타율 (ex: 0.368)
    private String inningPitched; // (투수만) 이닝 (ex: 0.2)
    private String totalPitches;  // (투수만) 투구수 (ex: 16)
    private String summaryText;    // (직전타자만)현재 타석 결과 (ex: 삼진 아웃)
    private List<String> pitchResults; // (직전타자만) 투구 결과 리스트 (ex: ["스트라이크", "볼", "파울", ...])
}
