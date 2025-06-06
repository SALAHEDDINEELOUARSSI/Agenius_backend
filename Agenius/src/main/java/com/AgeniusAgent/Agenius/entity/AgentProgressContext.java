package com.AgeniusAgent.Agenius.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class AgentProgressContext {
    private final int totalTools;
    private final AtomicInteger executedTools = new AtomicInteger(0);

    public AgentProgressContext(int totalTools) {
        this.totalTools = totalTools;

    }

    public int incrementAndGetProgress() {
        int current = executedTools.incrementAndGet();
        return (current * 100) / totalTools;
    }

    public void setProgress() {
        executedTools.set(0);
    }
}