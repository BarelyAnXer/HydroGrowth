package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 1;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DEVICE_ADDRESS = "00:21:09:00:09:D1"; // Replace with your HC-05 address

    private InputStream inputStream;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connectButton = findViewById(R.id.connectButton);
        textView = findViewById(R.id.textView);

        connectButton.setOnClickListener(view -> {
            try {
                Log.d("BluetoothConnection", "Attempting to connect...");

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    Log.d("BluetoothConnection", "Bluetooth is enabled.");
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("BluetoothConnection", "Bluetooth permission not granted.");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
                        return;
                    }

                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();
                    outputStream = socket.getOutputStream();

                    // Inside the connectButton click listener after outputStream setup
                    inputStream = socket.getInputStream();

                    Log.d("BluetoothConnection", "Connected successfully.");

                    startReadingData();
                } else {
                    Log.d("BluetoothConnection", "Bluetooth is not enabled.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BluetoothConnection", "Error while connecting: " + e.getMessage());
            }
        });

        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            if (outputStream != null) {
                try {
                    outputStream.write("1".getBytes()); // Send "1" to Arduino
                    Log.d("BluetoothSend", "Sent: 1"); // Log the sent message
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("BluetoothSend", "Error while sending: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now perform your Bluetooth operation.
            } else {
                // Permission denied, handle the situation accordingly (e.g., show a message to the user).
            }
        }
    }




    private boolean isReading = false; // A flag to control reading

    private void startReadingData() {
        if (isReading) {
            Log.d("ArduinoReading", "Already reading data.");
            return;
        }

        isReading = true;

        try {
            inputStream = socket.getInputStream();
            Log.d("ArduinoReading", "Input stream initialized successfully");

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while (isReading) {
                    try {
                        bytesRead = inputStream.read(buffer);
                        if (bytesRead > 0) {
                            String data = new String(buffer, 0, bytesRead);
                            Log.d("ArduinoReading", "Received: " + data);

                            // Update the TextView on the UI thread
                            runOnUiThread(() -> {
                                textView.setText("Received: " + data);
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("ArduinoReading", "Error while reading data: " + e.getMessage());
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ArduinoReading", "Error while initializing input stream: " + e.getMessage());
        }
    }

// ...

    private void stopReadingData() {
        isReading = false;
    }


}
