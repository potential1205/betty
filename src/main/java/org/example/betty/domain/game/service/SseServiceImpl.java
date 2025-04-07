package org.example.betty.domain.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
public class SseServiceImpl implements SseService {

    private final Map<String, Set<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    @Override
    public SseEmitter stream(String gameId) {
        SseEmitter emitter = new SseEmitter(0L); // 무제한 연결 유지

        emitterMap.computeIfAbsent(gameId, key -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> {
            removeEmitter(gameId, emitter);
            log.info("SSE 연결 종료: gameId={}", gameId);
        });

        emitter.onTimeout(() -> {
            removeEmitter(gameId, emitter);
            log.info("SSE 타임아웃: gameId={}", gameId);
        });

        emitter.onError(e -> {
            removeEmitter(gameId, emitter);
            log.warn("SSE 오류 발생: gameId={} | {}", gameId, e.getMessage());
        });

        log.info("SSE 연결 수립: gameId={} | 현재 연결 수={}", gameId, emitterMap.get(gameId).size());
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
}
