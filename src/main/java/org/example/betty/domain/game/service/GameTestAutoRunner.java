package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameTestAutoRunner implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate2;
    private final GameRelayEventHandler gameRelayEventHandler;

    private static final String GAME_ID = "20250410HHOB02025";
    private static final String REDIS_KEY = "games:2025-04-10:20250410HHOB02025";
    private final AtomicInteger inningCounter = new AtomicInteger(1);

    private final List<String> batterNames = Arrays.asList("박민우", "손아섭", "이명기", "노진혁", "김성욱");

    @Override
    public void run(String... args) {
        log.info("[자동 테스트] GameTestAutoRunner 시작됨! 5초마다 relay 자동 전송 테스트 중...");
    }

    @Scheduled(fixedRate = 10000)
    public void pushTestRelay() {
        int inningNum = inningCounter.getAndIncrement();

        String inningText = (inningNum % 2 == 1) ? (inningNum / 2 + 1) + "회초" : (inningNum / 2) + "회말";
        String scoreText = "NC " + (inningNum % 5 + 1) + " : 키움 " + (inningNum % 3);

        String prevName = batterNames.get((inningNum - 1 + batterNames.size()) % batterNames.size());
        String batterName = batterNames.get(inningNum % batterNames.size());

        RedisGameRelay relay = RedisGameRelay.builder()
                .inning(inningText)
                .teamAtBat("NC공격")
                .score(scoreText)
                .outCount(2)
                .pitchResult(Arrays.asList("스트라이크", "볼"))
                .runnerOnBase(Arrays.asList("1루 1루 \n권희동"))
                .pitcher(PlayerRelayInfo.builder()
                        .name("정현우")
                        .position("투수(좌투)")
                        .inningPitched("2.2")
                        .totalPitches("57(스트라이크 37+볼20)")
                        .build())
                .batter(PlayerRelayInfo.builder()
                        .name(batterName)
                        .position((inningNum % 9 + 1) + "번타자(좌타)")
                        .avg("타율 0.3" + (inningNum % 10))
                        .build())
                .previousBatter(PlayerRelayInfo.builder()
                        .name(prevName)
                        .position("이전타자")
                        .avg("타율 0.2" + (inningNum % 10))
                        .summaryText(prevName + " : 삼진 아웃")
                        .pitchResults(Arrays.asList("헛스윙", "파울", "파울", "스트라이크", "볼", "스트라이크"))
                        .build())
                .nextBatters(Arrays.asList("4번 데이비슨", "5번 박건우", "6번 한재환"))
                .build();

        redisTemplate2.opsForHash().put(REDIS_KEY, "relay", relay);

        gameRelayEventHandler.handleGameInfoChange(GAME_ID, relay);
        gameRelayEventHandler.handleRelayUpdate(GAME_ID, relay);

        log.info("[자동 테스트] relay 저장 + 소켓 전송 완료 | 이닝: {} | 타자: {} | 점수: {}", inningText, batterName, scoreText);
    }
}
