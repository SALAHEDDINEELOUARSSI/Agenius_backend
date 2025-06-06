package com.AgeniusAgent.Agenius.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CandidatsAccepted")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class CandidatsAccepted {
    @Id
    private String id;
    private String email;
    private String jobName;
    private String createdBy;
    private String phone;
    private int score; // Ajouter un champ pour le score du candidat
    private String name; // Ajouter un champ pour le nom du candidat

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
