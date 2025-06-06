package  com.AgeniusAgent.Agenius.entity;

import org.springframework.data.annotation.Id;

public class Choice {
    @Id
    private Long id;
    private String choices;
    private int result;
    public Choice() {}
    public Choice(String choices, int result) {
        this.choices = choices;
        this.result = result;
    }
    public Long getId() {
        return id;
    }
    public String getChoices() {
        return choices;
    }
    public void setChoices(String choices) {
        this.choices = choices;
    }
    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }
}
