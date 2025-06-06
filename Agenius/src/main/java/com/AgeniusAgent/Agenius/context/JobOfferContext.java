package com.AgeniusAgent.Agenius.context;


public class JobOfferContext {

    private static final ThreadLocal<String> jobOfferNameHolder = new ThreadLocal<>();

    public static void setJobOfferName(String name) {
        jobOfferNameHolder.set(name);
    }

    public static String getJobOfferName() {
        return jobOfferNameHolder.get();
    }

    public static void clear() {
        jobOfferNameHolder.remove();
    }
}
