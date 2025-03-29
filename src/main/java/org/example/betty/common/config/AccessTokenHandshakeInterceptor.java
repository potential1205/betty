package org.example.betty.common.config;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AccessTokenHandshakeInterceptor implements HandshakeInterceptor {

    private final SessionUtil sessionUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        List<String> authHeaders = request.getHeaders().get("Authorization");
        String token = (authHeaders != null && !authHeaders.isEmpty()) ? authHeaders.get(0) : null;

        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            String walletAddress = sessionUtil.getSession(token);
            attributes.put("walletAddress", walletAddress);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}