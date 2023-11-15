package com.example.myapplication;
import java.util.Random;

public class DailyTip {
    private String content;

    public DailyTip(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Static method to generate a random tip from an array
    public static DailyTip generateRandomTip(String[] tipsArray) {
        Random random = new Random();
        int index = random.nextInt(tipsArray.length);
        return new DailyTip(tipsArray[index]);
    }
}
