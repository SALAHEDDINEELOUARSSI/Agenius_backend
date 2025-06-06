package com.AgeniusAgent.Agenius.events;


import java.io.Serializable;

public class DeadlineEvent  {
    private String offerName;
    private int phaseStatus;

    public DeadlineEvent() {}

    public DeadlineEvent(String offerName, int phaseStatus) {
        this.offerName = offerName;
        this.phaseStatus = phaseStatus;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public int getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(int phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    @Override
    public String toString() {
        return "DeadlineEvent{" +
                "offerName='" + offerName + '\'' +
                ", phaseStatus=" + phaseStatus +
                '}';
    }
}

