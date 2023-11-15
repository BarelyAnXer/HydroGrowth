package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String[] TIPS_ARRAY = {
            "Don't forget to stay hydrated!",
            "Take breaks to stretch and move around.",
            "Get a good night's sleep for better productivity.",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DailyTip dailyTip = DailyTip.generateRandomTip(TIPS_ARRAY);
        showDailyTipPrompt(dailyTip);

        TextView txtViewPh = findViewById(R.id.textView);
        TextView txtViewTemp = findViewById(R.id.textView2);
        LinearLayout openPhValue = findViewById(R.id.phValueContainer);
        LinearLayout openTemperature = findViewById(R.id.temperatureContainer);

        // Initialize Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sensorValues");

        databaseReference.child("ph").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phValue = dataSnapshot.getValue(String.class);
                String concat = "ph: " + phValue;
                txtViewPh.setText(concat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                // Handle the error
            }
        });

        databaseReference.child("tds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tempValue = dataSnapshot.getValue(String.class);
                String concat = "temp: " + tempValue;
                txtViewTemp.setText(concat);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        openPhValue.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, PhValueActivity.class);
            MainActivity.this.startActivity(myIntent);
        });

        openTemperature.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, TemperatureActivity.class);
            MainActivity.this.startActivity(myIntent);
        });


    }

    private void showDailyTipPrompt(DailyTip dailyTip) {
        // Inflate the daily tip prompt layout
        View dailyTipView = getLayoutInflater().inflate(R.layout.daily_tip_prompt, null);

        // Set tip content
        TextView tipContentTextView = dailyTipView.findViewById(R.id.tipContent);
        tipContentTextView.setText(dailyTip.getContent());

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dailyTipView);

        // Close button click listener
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }
}