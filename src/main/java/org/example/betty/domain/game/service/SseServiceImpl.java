package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.game.entity.Game;
import org.example.betty.domain.game.repository.GameRepository;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseServiceImpl implements SseService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;
    private final Map<String, Set<SseEmitter>> emitterMap = new ConcurrentHashMap<>();
    private final GameRepository gameRepository;


    @Override
    public SseEmitter stream(Long gameId) {

        Optional<Game> optionalGame = gameRepository.findById(gameId);
        String gameCode = generateGameId(optionalGame.get());

        SseEmitter emitter = new SseEmitter(0L); // 무제한 연결 유지

        emitterMap.computeIfAbsent(gameCode, key -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> {
            removeEmitter(gameCode, emitter);
            log.info("SSE 연결 종료: gameId={}", gameCode);
        });

        emitter.onTimeout(() -> {
            removeEmitter(gameCode, emitter);
            log.info("SSE 타임아웃: gameId={}", gameCode);
        });

        emitter.onError(e -> {
            removeEmitter(gameCode, emitter);
            log.warn("SSE 오류 발생: gameId={} | {}", gameCode, e.getMessage());
        });

        log.info("SSE 연결 수립: gameId={} | 현재 연결 수={}", gameId, emitterMap.get(gameCode).size());
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
        } catch (IOException e) {
            log.warn("SSE 초기 메시지 전송 실패: gameId={} | {}", gameId, e.getMessage());
        }
        return emitter;
    }

    @Override
    public void send(String gameId, Object data) {
        String eventName = resolveEventName(data);
        Set<SseEmitter> emitters = emitterMap.get(gameId);

        if (emitters == null || emitters.isEmpty()) {
            log.warn("SSE 전송 실패: 연결 없음 - gameId={}", gameId);
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
                log.info("SSE 전송 성공: gameId={} | event={} | data={}", gameId, eventName, data.toString());

                if ("ENDED".equals(data)) {
                    emitter.complete();
                    disconnect(gameId, emitter);
                }

            } catch (IOException e) {
                disconnect(gameId, emitter);
                log.warn("SSE 전송 실패: gameId={} | {}", gameId, e.getMessage());
            }
        }
    }

    @Override
    public void disconnect(String gameId, SseEmitter emitter) {
        removeEmitter(gameId, emitter);
        log.info("SSE 연결 수동 종료: gameId={}", gameId);
    }

    private void removeEmitter(String gameId, SseEmitter emitter) {
        Set<SseEmitter> emitters = emitterMap.get(gameId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emitterMap.remove(gameId);
            }
        }
    }

    private String resolveEventName(Object data) {
        if (data instanceof String stringData) {
            if ("ENDED".equals(stringData)) return "end";
            if (stringData.contains("회")) return "inning";
            if (stringData.contains(":")) return "score";
        }
        return "problem";
    }

    private String generateGameId(Game game) {
        return game.getGameDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + game.getAwayTeam().getTeamCode()
                + game.getHomeTeam().getTeamCode()
                + "0" + game.getSeason();
    }
}
