package org.example.betty.common.config;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.display.entity.DisplayAccess;
import org.example.betty.domain.display.repository.DisplayAccessRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionUtil sessionUtil;
    private final DisplayAccessRepository displayAccessRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new AccessTokenHandshakeInterceptor(sessionUtil, displayAccessRepository));
    }
}
