package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TemperatureActivity extends AppCompatActivity {
    private static final String TAG = "tempactivity";
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature_value);

        listView = findViewById(R.id.listView);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textViewDate, dataList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();
    }

    private void loadDataFromFirestore() {
        db.collection("sensorData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            // Sort the documents by timestamp in descending order
                            Collections.sort(documents, new Comparator<DocumentSnapshot>() {
                                @Override
                                public int compare(DocumentSnapshot doc1, DocumentSnapshot doc2) {
                                    String timestamp1 = doc1.getString("timestamp");
                                    String timestamp2 = doc2.getString("timestamp");

                                    // Convert timestamp strings to ISO 8601 format for comparison
                                    LocalDateTime dt1 = LocalDateTime.parse(timestamp1, DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy HH:mm:ss", Locale.ENGLISH));
                                    LocalDateTime dt2 = LocalDateTime.parse(timestamp2, DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy HH:mm:ss", Locale.ENGLISH));

                                    return dt2.compareTo(dt1); // Compare in descending order
                                }
                            });

                            // Add sorted data to dataList
                            for (DocumentSnapshot document : documents) {
                                String date = document.getString("timestamp");
                                String value = document.getString("celsius");
                                dataList.add("Temperature: " + value + "°C" + "\n" + date);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}

