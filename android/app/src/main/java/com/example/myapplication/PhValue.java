package com.example.myapplication;

import java.util.Date;

public class PhValue {
    private String phValue;
    private Date date;

    public PhValue(String phValue, Date date) {
        this.phValue = phValue;
        this.date = date;
    }

    public String getPhValue() {
        return phValue;
    }

    public Date getDate() {
        return date;
    }
}
