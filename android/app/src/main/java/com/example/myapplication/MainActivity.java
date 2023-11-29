package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String[] TIPS_ARRAY = {
            "Choose your desired lettuce seeds",
            " Prepare your seedling tray and medium (Cocopeat or Sponge)",
            "Fill your tray with cocopeat and poke a small hole for the seed.",
            "Water your cocopeat enough to make it wet. (Do not overwater).",
            "After 72 hours or 3 days of Germinating you will see the cotyledon (2 small leaves).",
            "Expose them to full sunlight and start bottom watering them with half strength solution at day 7.",
            "15 days after sowing, your lettuce are ready to transplant to the netcups.",
            "Prepare your tuna box and water with full strength nutrient solution.",
            "Make sure only the bottom of the netcups is submerged in the water.",
            "Monitor them using the application for the changes in pH, temperature, water level, and nutrient solution.",
            "Wait for 30 days after transplanting before harvesting your lettuce.",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("arduino_notifications");

        DailyTip dailyTip = DailyTip.generateRandomTip(TIPS_ARRAY);
        showDailyTipPrompt(dailyTip);

        TextView txtViewPh = findViewById(R.id.textView1);
        TextView txtViewTemp = findViewById(R.id.textView2);
        TextView txtViewWaterLevel = findViewById(R.id.textView3);
        TextView txtViewTDS = findViewById(R.id.textView4);
        LinearLayout openPhValue = findViewById(R.id.phcontainer);
        LinearLayout openTemperature = findViewById(R.id.tempcontainer);
        LinearLayout openWater = findViewById(R.id.watercontainer);
        LinearLayout openNutrient = findViewById(R.id.nutrientcontainer);
        Button btnImageGallery = findViewById(R.id.insertImgButton);

        // Initialize Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sensorValues");

        databaseReference.child("ph").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phValue = dataSnapshot.getValue(String.class);
                txtViewPh.setText(phValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        databaseReference.child("tds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tempValue = dataSnapshot.getValue(String.class);
                txtViewTDS.setText(tempValue);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        databaseReference.child("waterLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tempValue = dataSnapshot.getValue(String.class);
                txtViewWaterLevel.setText(tempValue);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        databaseReference.child("celsius").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tempValue = dataSnapshot.getValue(String.class);
                txtViewTemp.setText(tempValue + "°C");
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

        openNutrient.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, NutrientActivity.class);
            MainActivity.this.startActivity(myIntent);
        });

        openWater.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, WaterActivity.class);
            MainActivity.this.startActivity(myIntent);
        });


        btnImageGallery.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ImageGallery.class));
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