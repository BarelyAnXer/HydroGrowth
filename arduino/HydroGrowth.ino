#include <DallasTemperature.h>
#include <OneWire.h>
// temperature libraries

#include <Wire.h>

#include <Arduino.h>
#include <ESP8266WiFi.h>

#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "RADIUSAD549"
#define WIFI_PASSWORD "NL9yXrsS9G"

// Insert Firebase project API Key
#define API_KEY "AIzaSyBRG8rQ5IARNBay2SdQvbMJ9a52TkvDrwQ"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://hydrogrowth-420be-default-rtdb.firebaseio.com/"

#define SensorPin A0

// Define Firebase Data object
FirebaseData firebaseData;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
int count = 0;
bool signupOK = false;

const int analogInPin = A0;
int phSensorValue = 0;



#define ONE_WIRE_BUS 4
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);



void setup() {
  Serial.begin(9600);

  sensors.begin();
  pinMode(2, OUTPUT);


  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback;  //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  // FirebaseJson content;
  // String documentPath = "a0/b" + String(count);

  // if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)) {
  //   sendDataPrevMillis = millis();

  phSensorValue = analogRead(analogInPin);
  Serial.println("Water Level: " + String(phSensorValue));
  delay(1000);

  // Start of printing the temperatures
  sensors.requestTemperatures();
  Serial.println("Celsius temperature: " + String(sensors.getTempCByIndex(0)));
  Serial.println("Fahrenheit temperature: " + String(sensors.getTempFByIndex(0)));
  Serial.println("pH Value: " + String(calculatepHValue(SensorPin), 2));

  Firebase.RTDB.setString(&firebaseData, "sensorValues/ph", String(calculatepHValue(SensorPin), 2));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/celsius", String(sensors.getTempCByIndex(0)));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/farenheit", String(sensors.getTempFByIndex(0)));
  // Firebase.RTDB.setString(&firebaseData, "sensorValues/farenheit", String(sensors.getTempFByIndex(0)));
  // Firebase.RTDB.setString(&firebaseData, "sensorValues/farenheit", String(sensors.getTempFByIndex(0)));
  

  // content.set("fields/myLatLng/geoPointValue/latitude", 1.486284);
  // content.set("fields/myLatLng/geoPointValue/longitude", 23.678198);

  // Firebase.Firestore.createDocument(&firebaseData, "hydrogrowth-420be", "" /* databaseId can be (default) or empty */, documentPath.c_str(), content.raw());
  // Serial.printf("ok\n%s\n\n", firebaseData.payload().c_str());
  // }
}

float calculatepHValue(int phSensorPin) {
  int buf[10];
  int temp;
  float averageValue = 0;

  for (int i = 0; i < 10; i++) {
    buf[i] = analogRead(phSensorPin);
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

  for (int i = 2; i < 8; i++) {
    averageValue += buf[i];
  }

  float phValue = (float(averageValue) * 5.0 / 1024 / 6) * 3.5;
  return phValue;
}
