package com.AgeniusAgent.Agenius.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "candidats_qcm")
public class CandidatQCM {
    @Id
    private String id;
    private String email;
    private int score;
    public CandidatQCM() {}
    public CandidatQCM(String email, int score) {
        this.email = email;
        this.score = score;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
