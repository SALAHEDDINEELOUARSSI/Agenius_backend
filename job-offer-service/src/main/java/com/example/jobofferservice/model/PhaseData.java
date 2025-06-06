package com.example.jobofferservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

//cette collection stocke dans phaseStatus
//(phase 1 juste qui est selected  => 0 ,phase 1 et selected => 1)
// deadline juste si les 2 phases sont selected stocke le delai
//offerName stocke loffre qui est selected dans importcv

@Data
@Document(collection = "PhaseData")
public class PhaseData {
    
    @Id
    private String id;
    private int phaseStatus;
    private int phase3;
    private String deadline;
    private String deadline2;
    private String offerName;
    private boolean emailSent;
    private boolean phase3reashed;
    public PhaseData(int phaseStatus, String deadline,String deadline2) {
        this.phaseStatus = phaseStatus;
        this.deadline = deadline;
        this.deadline2 = deadline2;

    }
    // Getters et Setters
    public int getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(int phaseStatus) {
        this.phaseStatus = phaseStatus;
    }
    public int getPhase3() {
        return phase3;
    }
    public boolean isEmailSent() {
        return emailSent;
    }
    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public void setPhase3(int phase3) {
        this.phase3 = phase3;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }
    public String getDeadline2() {
        return deadline2;
    }

    public void setDeadline2(String deadline2) {
        this.deadline2 = deadline2;
    }
    public void setPhase3reashed(boolean phase3reashed) { this.phase3reashed = phase3reashed; }
    public boolean isPhase3reashed() {return phase3reashed;}
}