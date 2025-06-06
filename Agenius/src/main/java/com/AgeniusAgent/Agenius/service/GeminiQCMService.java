package com.AgeniusAgent.Agenius.service;

import com.AgeniusAgent.Agenius.entity.Qcm;
import com.AgeniusAgent.Agenius.entity.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.AgeniusAgent.Agenius.service.OffresService;
import com.AgeniusAgent.Agenius.Model.Offres;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiQCMService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    @Autowired
    private OffresService offresRepository;
    private final RestTemplate restTemplate ;
    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiQCMService(@Qualifier("externalRestTemplate") RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }


    public List<Question> generateQuestions(String offreName) throws IOException {

        Offres offre = offresRepository.findOffresByName(offreName);//khass tbdl
        String competences=offre.getDescription();

        String prompt = "Generate 20 multiple-choice questions with one correct answer on the following skills: " + competences +
                ". The response must be formatted as valid JSON and include objects structured exactly like this: " +
                "{ 'question': 'What is the capital of France?', 'choices': [ " +
                "{ 'choices': 'Paris', 'result': 10 }, { 'choices': 'London', 'result': 0 }, { 'choices': 'Berlin', 'result': 0 } ] }." +
                "Make sure to follow this exact JSON structure without adding extra fields and The questions should be complex and Shuffle the answer choices so that the correct one is not always in the first position.";
        prompt = prompt.replace("\"", "\\\"");

        String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";

        // Log pour vérifier l'URL de l'API et le corps de la requête
        //  System.out.println("URL de l'API Gemini : " + GEMINI_API_URL + "?key=" + apiKey);
        // System.out.println("Corps de la requête : " + jsonBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        // Créer l'entité avec le corps de la requête et les entêtes
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        // Envoyer la requête POST à Gemini
        ResponseEntity<String> response = restTemplate.exchange(
                GEMINI_API_URL + "?key=" + apiKey,
                HttpMethod.POST,
                entity,
                String.class
        );


        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Erreur lors de la requête à Gemini : " + response.getStatusCode());
        }
        // Récupérer la réponse de Gemini
        String responseBody = response.getBody();
        System.out.println("Réponse de Gemini : " + responseBody);

        // Vérification du format JSON avant de parser
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("La réponse de Gemini est vide.");
        }
        // Récupérer et traiter la réponse de Gemini
        return parseQuestionsFromGemini(response.getBody());
    }

    //cette fct prend en entrée une réponse JSON brute provenant de Gemini (un modèle d'IA) et la convertit en une liste d'objets
    private List<Question> parseQuestionsFromGemini(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Affiche la réponse brute pour voir ce que nous recevons
        System.out.println("Réponse brute de Gemini : " + jsonResponse);
        // Nettoyer la réponse en enlevant les backticks et les caractères indésirables
        jsonResponse = jsonResponse.replaceAll("```json\\n|\\n```", "");
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(jsonResponse);
        } catch (JsonParseException e) {
            throw new IOException("Erreur de parsing JSON : " + e.getMessage());
        }

        List<Question> questions = new ArrayList<>();

        // Extraire le texte de la réponse générée par Gemini
        String generatedText = rootNode.path("candidates").get(0).path("content")
                .path("parts").get(0).path("text").asText();

        if (generatedText.isEmpty()) {
            throw new IOException("Aucune question générée par Gemini.");
        }
        // Nettoyer et parser les questions
        generatedText = generatedText.replaceAll("```json\\n|\\n```", "");
        // Convertir le texte JSON en liste de questions
        questions = objectMapper.readValue(generatedText, objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));

        return questions;
    }

    public Qcm generateQcmForCandidate(String candidateId, String jobTitle) {
        Qcm qcm = new Qcm();
        String testLink = "http://localhost:8080/qcm/" + candidateId;  // Générer le lien avec l'ID du candidat
        qcm.setTestLink(testLink);
        return qcm;
    }
}