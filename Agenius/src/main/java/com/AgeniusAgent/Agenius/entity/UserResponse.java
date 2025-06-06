package com.AgeniusAgent.Agenius.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;
@Data
@AllArgsConstructor
@Document(collection = "user_responses")
public class UserResponse {
    @Id
    private String id;
    private String email;

    private Map<String, Integer> responses; // Clé : "q1", Valeur : 10 ou 0    private List<String> responses; // Liste des réponses au format "1-selected answer"

    private int score;
    private String offrename;
    private String phone;

    // Constructeurs
    public UserResponse() {}

    public UserResponse(String email, Map<String, Integer> responses) {
        this.email = email;
        this.responses = responses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  Map<String, Integer> getResponses() {
        return responses;
    }
    public void setResponses(Map<String, Integer> responses) {
        this.responses = responses;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getOffrename() {
        return offrename;
    }

    public void setOffrename(String offrename) {
        this.offrename = offrename;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
