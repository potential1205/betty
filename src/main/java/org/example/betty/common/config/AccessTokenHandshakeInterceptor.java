package org.example.betty.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.display.repository.DisplayAccessRepository;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class AccessTokenHandshakeInterceptor implements HandshakeInterceptor {

    private final SessionUtil sessionUtil;
    private final DisplayAccessRepository displayAccessRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        URI uri = request.getURI();
        log.info("Handshake request URI: {}", uri);

        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
        String token = queryParams.getFirst("access_token");
        if (token == null || token.trim().isEmpty()) {
            log.warn("Missing or empty access_token parameter");
            return false;
        }
        token = "Bearer " + token;

        String gameIdStr = queryParams.getFirst("game_id");
        String teamIdStr = queryParams.getFirst("team_id");
        if (gameIdStr == null || teamIdStr == null) {
            log.warn("Missing game_id or team_id parameters: game_id={}, team_id={}", gameIdStr, teamIdStr);
            return false;
        }

        Long gameId;
        Long teamId;
        try {
            gameId = Long.valueOf(gameIdStr);
            teamId = Long.valueOf(teamIdStr);
        } catch (NumberFormatException e) {
            log.error("Invalid format for game_id or team_id: game_id={}, team_id={}", gameIdStr, teamIdStr, e);
            return false;
        }

        try {
            String walletAddress = sessionUtil.getSession(token);
            log.info("Retrieved walletAddress: {} for token", walletAddress);

            if (!displayAccessRepository.existsByWalletAddressAndGameIdAndTeamId(walletAddress, gameId, teamId)) {
                log.warn("Display access not found for walletAddress={}, gameId={}, teamId={}", walletAddress, gameId, teamId);
                return false;
            }

            attributes.put("walletAddress", walletAddress);
            log.info("Handshake successful for walletAddress: {}", walletAddress);
            return true;
        } catch (Exception e) {
            log.error("Exception during handshake processing", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("AfterHandshake encountered an error", exception);
        } else {
            log.info("AfterHandshake completed successfully");
        }
    }
}