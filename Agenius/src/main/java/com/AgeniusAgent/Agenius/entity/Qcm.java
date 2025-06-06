package com.AgeniusAgent.Agenius.entity;

public class Qcm {
    private String testLink;

    // Constructeurs
    public Qcm() {}
    public Qcm(String testLink) {
        this.testLink = testLink;
    }
    // Getters et Setters
    public String getTestLink() {
        return testLink;
    }
    public void setTestLink(String testLink) {
        this.testLink = testLink;
    }
}
