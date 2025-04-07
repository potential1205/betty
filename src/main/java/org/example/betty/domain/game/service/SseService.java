package org.example.betty.domain.game.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    // 클라이언트가 gameId에 대한 실시간 스트리밍을 요청할 때
    SseEmitter stream(String gameId);

    // 해당 gameId로 연결된 모든 클라이언트에게 데이터 전송 (문제든, 경기 종료든)
    void send(String gameId, Object data);

    // 연결 종료 처리
    void disconnect(String gameId, SseEmitter emitter); // 선택적
}
