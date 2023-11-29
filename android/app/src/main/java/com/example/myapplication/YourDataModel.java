package com.example.myapplication;

// YourDataModel.java

import com.google.firebase.Timestamp;

public class YourDataModel {
    private Timestamp date; // Firebase Timestamp for storing date and time
    private String value;

    public YourDataModel() {
        // Required empty public constructor for Firestore to deserialize
    }

    public YourDataModel(Timestamp date, String value) {
        this.date = date;
        this.value = value;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

