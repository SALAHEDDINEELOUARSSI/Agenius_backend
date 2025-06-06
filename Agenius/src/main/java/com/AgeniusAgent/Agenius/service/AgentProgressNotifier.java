package com.AgeniusAgent.Agenius.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AgentProgressNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public AgentProgressNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;

    }

    public void notifyProgress(String sessionId, String toolName, int progress) {
        messagingTemplate.convertAndSend("/topic/progress/" + sessionId,
                Map.of("tool", toolName, "progress", progress));
        
    }
}