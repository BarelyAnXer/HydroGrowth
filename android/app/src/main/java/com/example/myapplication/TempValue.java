package com.example.myapplication;

import java.util.Date;

public class TempValue {
    private String tempValue;
    private Date date;

    public TempValue(String tempValue, Date date) {
        this.tempValue = tempValue;
        this.date = date;
    }

    public String getTempValue() {
        return tempValue;
    }

    public Date getDate() {
        return date;
    }
}
