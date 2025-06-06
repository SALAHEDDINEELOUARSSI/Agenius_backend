package com.AgeniusAgent.Agenius.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-progress")
        .setAllowedOriginPatterns("http://localhost:3000") // Autoriser les requêtes depuis le frontend
        .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Pour les messages envoyés au client
        registry.setApplicationDestinationPrefixes("/app"); // Pour les messages envoyés depuis le client
    }
}