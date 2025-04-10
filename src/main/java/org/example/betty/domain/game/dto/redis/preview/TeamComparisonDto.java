package org.example.betty.domain.game.dto.redis.preview;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamComparisonDto {
    private TeamSummaryInfo awayTeam;        // 한화
    private TeamSummaryInfo homeTeam;        // 두산

    private RecentGameResult awayRecent;     // 한화 최근경기
    private RecentGameResult homeRecent;     // 두산 최근경기

    private TeamStat awayStat;               // 한화 주요지표
    private TeamStat homeStat;               // 두산 주요지표

    private HeadToHeadRecord awayRecord;     // 한화 상대전적
    private HeadToHeadRecord homeRecord;     // 두산 상대전적
}
