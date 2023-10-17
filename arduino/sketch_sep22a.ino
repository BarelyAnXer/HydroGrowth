#include <SoftwareSerial.h>
SoftwareSerial bluetooth(11, 10);

#include <DallasTemperature.h>
#include <OneWire.h>
#include <Wire.h>
#include <DS18B20.h>

#define ONE_WIRE_BUS 2
#define SensorPin A1
unsigned long int avgValue;
float b;
int buf[100], temp;

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

bool isConnected = false;  // Flag to track Bluetooth connection

void setup(void) {
  Serial.begin(9600);
  sensors.begin();
  pinMode(2, OUTPUT);
  Serial.begin(9600);
  Serial.println("Ready");

  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop(void) {
  if (bluetooth.available()) {
    char receivedChar = bluetooth.read();
    Serial.print(receivedChar);

    // Check if a device is connected via Bluetooth
    if (!isConnected) {
      // Serial.println("Device connected via Bluetooth");
      isConnected = true;  // Set the connection flag to true
    }
  } else {
    // No device is connected
    isConnected = false;  // Set the connection flag to false
  }

  if (Serial.available()) {
    String message = Serial.readString();  // Read the entire input as a string
    bluetooth.println(message);            // Send the message over Bluetooth
  }


  sensors.requestTemperatures();

  Serial.print("Celsius temperature: ");
  Serial.print(sensors.getTempCByIndex(0));
  Serial.print(" - Fahrenheit temperature: ");
  Serial.println(sensors.getTempFByIndex(0));
  delay(3000);

  for (int i = 0; i < 10; i++) {
    buf[i] = analogRead(SensorPin);
    delay(10);
  }
  for (int i = 0; i < 9; i++) {
    for (int j = i + 1; j < 10; j++) {
      if (buf[i] > buf[j]) {
        temp = buf[i];
        buf[i] = buf[j];
        buf[j] = temp;
      }
    }
  }
  avgValue = 0;
  for (int i = 2; i < 8; i++) {
    avgValue += buf[i];
  }
  float phValue = (float)avgValue * 5.0 / 1024 / 6;
  phValue = 3.5 * phValue;
  Serial.print("    pH Value:");
  Serial.print(phValue, 2);

  bluetooth.print("pH Value: ");
  bluetooth.println(phValue, 2);

  Serial.println(" ");
  digitalWrite(2, HIGH);
  delay(800);
  digitalWrite(2, LOW);
}