package com.jirathon.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.websocket.allowed-origin-patterns:http://localhost:*,https://*.jirathon.io}")
    private String websocketAllowedOriginPatterns;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable in-memory message broker for /topic and /queue
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for per-user messages
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedPatterns = Arrays.stream(websocketAllowedOriginPatterns.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new);

        // Register WebSocket endpoint
        registry.addEndpoint("/ws-notifications")
            .setAllowedOriginPatterns(allowedPatterns)
                .withSockJS();
        
        // Raw WebSocket endpoint without SockJS fallback
        registry.addEndpoint("/ws-notifications-raw")
            .setAllowedOriginPatterns(allowedPatterns);
    }
}
