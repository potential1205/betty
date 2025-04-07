package org.example.betty.domain.game.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    // [4], [5] 클라이언트가 gameId로 SSE 연결을 구독할 때
    SseEmitter subscribe(String gameId);

    // [6], [8] 해당 gameId로 연결된 모든 클라이언트에게 데이터 전송 (문제든, 경기 종료든)
    void send(String gameId, Object data);

    // [9] 수동 종료하거나, 서버에서 종료시 처리할 메서드가 필요하다면
    void disconnect(String gameId, SseEmitter emitter); // 선택적
}
