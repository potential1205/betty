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

    // 각 gameId에 대해 여러 클라이언트의 SseEmitter를 저장할 수 있도록 Set으로 관리
    private final Map<String, Set<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 SSE 연결을 구독할 때 호출됨.
     * 각 gameId에 대해 여러 클라이언트를 등록할 수 있음.
     */
    @Override
    public SseEmitter subscribe(String gameId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 연결 유지 시간: 1시간

        // emitter 세트 가져오거나 새로 생성
        emitterMap.computeIfAbsent(gameId, key -> new CopyOnWriteArraySet<>()).add(emitter);

        // 연결 종료 시 emitter 제거
        emitter.onCompletion(() -> {
            removeEmitter(gameId, emitter);
            log.info("SSE 연결 종료: gameId={}", gameId);
        });

        // 타임아웃 시 emitter 제거
        emitter.onTimeout(() -> {
            removeEmitter(gameId, emitter);
            log.info("SSE 연결 타임아웃: gameId={}", gameId);
        });

        log.info("SSE 연결 수립: gameId={} | 현재 연결 수={}", gameId, emitterMap.get(gameId).size());
        return emitter;
    }

    /**
     * 특정 gameId를 구독 중인 모든 클라이언트에게 데이터를 전송
     */
    @Override
    public void send(String gameId, Object data) {
        Set<SseEmitter> emitters = emitterMap.get(gameId);

        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("new-problem")
                            .data(data));

                    System.out.println("\uD83D\uDFE2 SSE 전송됨! gameId=" + gameId + " | 문제: " + data.toString());
                    log.info("[SSE 전송 성공] gameId={} | data={}", gameId, data);
                } catch (IOException e) {
                    removeEmitter(gameId, emitter); // 실패한 emitter 제거
                    log.error("[SSE 전송 실패] gameId={} | {}", gameId, e.getMessage());
                }
            }
        }
    }

    @Override
    public void disconnect(String gameId, SseEmitter emitter) {

    }

    /**
     * emitterMap에서 특정 emitter를 제거
     */
    private void removeEmitter(String gameId, SseEmitter emitter) {
        Set<SseEmitter> emitters = emitterMap.get(gameId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emitterMap.remove(gameId); // 모두 제거되면 맵에서도 제거
            }
        }
    }
}
