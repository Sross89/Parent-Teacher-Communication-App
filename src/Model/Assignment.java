package Model;

import java.util.ArrayList;

public class Assignment {

    private String name;
    private String maxScore;
    private String percentage;

    public Assignment() {
    }

    public Assignment(String name, String scoreAvaliable, String percentage) {
        this.name = name;
        this.maxScore = scoreAvaliable;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String scoreAvaliable) {
        this.maxScore = scoreAvaliable;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}