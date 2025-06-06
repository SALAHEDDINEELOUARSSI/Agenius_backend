package com.AgeniusAgent.Agenius.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "questions")
public class Question {

    @Id
    private Long id;
    private String question;
    private List<Choice> choices;
    public Question() {
    }

    public Question(@JsonProperty("question") String question,
                    @JsonProperty("choices") List<Choice> choices
    ) {
        this.question = question;
        this.choices = choices;
    }

    public Long getId() {
        return id;
    }



    // Getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

}
