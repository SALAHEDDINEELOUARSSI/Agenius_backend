package com.example.interviewservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class GeminiService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private final String apiKey = "AIzaSyAe7FQdK_J3VHCABxLbELxx9FjXDCKJI8o";
    private final RestTemplate restTemplate;

    private final Map<String, ChatMemory> memories = new java.util.concurrent.ConcurrentHashMap<>();

    public GeminiService(@Qualifier("externalRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String continueHrInterview(String userPrompt, String phoneNumber,String offrename) throws IOException {
        ChatMemory chatMemory = memories.computeIfAbsent(phoneNumber, k -> {
            ChatMemory newMemory = new ChatMemory();
            String systemPrompt = """
            Tu es un recruteur RH dans une entreprise technologique.
            Tu réalises un entretien pour un poste de """+offrename+"""
            Analyse la réponse du candidat ci-dessous, puis pose 3 questions mais une par une.
            Tu poses chaque question après avoir reçu la réponse précédente.
            Ne donne pas la note pendant l’entretien, mais mémorise-la.
            À la fin de l’entretien, un système t'enverra 'Évaluation finale ?' pour te demander une note sur 100.
        """;
            newMemory.addUserMessage(systemPrompt);
            return newMemory;
        });

        chatMemory.addUserMessage(userPrompt);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> fullPrompt = Map.of("contents", chatMemory.getMessages());
        String jsonPayload = mapper.writeValueAsString(fullPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GEMINI_API_URL + "?key=" + apiKey,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Erreur Gemini : " + response.getStatusCode());
        }

        String responseText = extractTextFromGeminiResponse(response.getBody());
        chatMemory.addAssistantMessage(responseText);
        return responseText;
    }

//    public int getFinalScore(String phoneNumber) throws IOException {
//        return parseScore(continueHrInterview("Évaluation finale ?", phoneNumber));
//    }


    private int parseScore(String text) {
        // Exemple de parsing simple pour retrouver un score numérique
        return text.chars()
                .filter(Character::isDigit)
                .map(c -> c - '0')
                .reduce((acc, digit) -> acc * 10 + digit)
                .orElse(0);
    }

    private String extractTextFromGeminiResponse(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        String result = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        if (result == null || result.isEmpty()) {
            throw new IOException("Réponse Gemini invalide ou vide.");
        }

        return result.trim();
    }
}
