package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemperatureActivity extends AppCompatActivity {
    private TextView tempTextView;
    private FirebaseFirestore db;
    private CollectionReference tempValuesCollection;

    private RecyclerView recyclerView;
    private TempValueAdapter adapter;
    private List<TempValue> tempValueList = new ArrayList<>();


    private final BroadcastReceiver tempValueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("NEW_TEMP_VALUE")) {
                String phValue = intent.getStringExtra("temp_value");
                tempTextView.setText(phValue);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature_value);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        tempValuesCollection = db.collection("tempValues");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TempValueAdapter(tempValueList);
        recyclerView.setAdapter(adapter);


        tempTextView = findViewById(R.id.mainval);

        Button saveTempButton = findViewById(R.id.SaveTempButton);

        saveTempButton.setOnClickListener(v -> {
            String tempValue = tempTextView.getText().toString();
            saveToFirestore(tempValue);
        });

        displayFirestoreData();
    }

    private void displayFirestoreData() {
        tempValuesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            tempValueList.clear(); // Clear previous data

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String tempValue = documentSnapshot.getString("tempValue");
                Date date = documentSnapshot.getDate("date");

                TempValue tempValueObj = new TempValue(tempValue, date);
                tempValueList.add(tempValueObj);
            }

            adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur while fetching data
        });
    }


    private void saveToFirestore(String tempValue) {
        Map<String, Object> data = new HashMap<>();
        data.put("tempValue", tempValue);
        data.put("date", new Date());

        tempValuesCollection
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    // Successful write to Firestore
                    // You can add a success message or any other action here
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that may have occurred
                });

        displayFirestoreData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(tempValueReceiver, new IntentFilter("NEW_TEMP_VALUE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(tempValueReceiver);
    }
}

