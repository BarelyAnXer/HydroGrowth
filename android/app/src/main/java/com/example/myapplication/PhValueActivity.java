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

public class PhValueActivity extends AppCompatActivity {
    private TextView phTextView;
    private FirebaseFirestore db;
    private CollectionReference phValuesCollection;

    private RecyclerView recyclerView;
    private PhValueAdapter adapter;
    private List<PhValue> phValueList = new ArrayList<>();



    private final BroadcastReceiver phValueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("NEW_PH_VALUE")) {
                String phValue = intent.getStringExtra("ph_value");
                phTextView.setText(phValue);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_value);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        phValuesCollection = db.collection("phValues");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhValueAdapter(phValueList);
        recyclerView.setAdapter(adapter);


        phTextView = findViewById(R.id.mainval);

        Button SavephButton = findViewById(R.id.SavephButton);

        SavephButton.setOnClickListener(v -> {
            String phValue = phTextView.getText().toString();
            saveToFirestore(phValue);
        });

        displayFirestoreData();
    }

    private void displayFirestoreData() {
        phValuesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            phValueList.clear(); // Clear previous data

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String phValue = documentSnapshot.getString("phValue");
                Date date = documentSnapshot.getDate("date");

                PhValue phValueObj = new PhValue(phValue, date);
                phValueList.add(phValueObj);
            }

            adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur while fetching data
        });
    }


    private void saveToFirestore(String phValue) {
        Map<String, Object> data = new HashMap<>();
        data.put("phValue", phValue);
        data.put("date", new Date());

        phValuesCollection
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
        registerReceiver(phValueReceiver, new IntentFilter("NEW_PH_VALUE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(phValueReceiver);
    }
}

