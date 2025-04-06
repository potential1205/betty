package org.example.betty.domain.game.dto.redis;

public enum QuestionCode {
    PITCH_COUNT,       // 이번 타석에서 몇 구 안에 승부가 날까요? ① 1~3구 ② 4~6구 ③ 7구 이상
    ON_BASE,           // 이번 타자가 출루할까요? (O,X)
    STRIKE_OUT,        // 이번 투수가 타자를 삼진으로 잡을까요? (O,X)
    STRIKE_SEQUENCE,   // 이번 타석에서 연속 스트라이크로 타자를 압도할까요? (O,X)
    HOME_RUN,          // 이번 타자가 홈런을 칠까요? (O,X)
    PITCHER_DUTY,    // (5이닝 시작 시점) 선발 투수가 몇 이닝까지 책임질까요? ① 5이닝 이하 ② 6~7이닝 ③ 8이닝 이상
    RECORD_RBI,        // (2루 주자 있을 때) 이번 타자가 타점을 기록할까요? (O/X)
    FIRST_ON_BASE,     // (이닝 시작 시점)이번 타자가 이번 이닝의 첫 출루자가 될까요? (O,X)
    NO_RUN_ALLOWED,     // (이닝 시작 시점) 이번 투수가 이번 이닝을 실점 없이 끝낼까요?(O,X)
    LAST_BATTER        // (9회 초가 끝나고 9회 말로 넘어가는 상황에서 점수가 동점일 때)  이번 이닝에서 마지막 타자가 될 가능성이 가장 높은 선수는 [선수 이름] 입니다. 이 선수가 정말로 마지막 타자가 될까요?" (O,X)
}
