package com.example.interviewservice.Service;

import java.util.*;

public class ChatMemory {
    private final List<Map<String, Object>> messages = new ArrayList<>();

    public void addUserMessage(String message) {
        messages.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", message))
        ));
    }

    public void addAssistantMessage(String message) {
        messages.add(Map.of(
                "role", "model",
                "parts", List.of(Map.of("text", message))
        ));
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
