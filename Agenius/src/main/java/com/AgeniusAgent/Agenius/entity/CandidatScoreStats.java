package com.AgeniusAgent.Agenius.entity;


public class CandidatScoreStats {
    private int reussis;
    private int moyens;
    private int echoues;

    public CandidatScoreStats(int reussis, int moyens, int echoues) {
        this.reussis = reussis;
        this.moyens = moyens;
        this.echoues = echoues;
    }

    public int getReussis() { return reussis; }
    public void setReussis(int reussis) { this.reussis = reussis; }

    public int getMoyens() { return moyens; }
    public void setMoyens(int moyens) { this.moyens = moyens; }

    public int getEchoues() { return echoues; }
    public void setEchoues(int echoues) { this.echoues = echoues; }
}
