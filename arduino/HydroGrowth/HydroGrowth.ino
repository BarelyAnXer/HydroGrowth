#include <WiFi.h>
#include "time.h"
#include "sntp.h"

#include <DallasTemperature.h>
#include <OneWire.h>
#include <Wire.h>

#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

const char* ssid = "HUAWEI-2.4G-3Vtx";
const char* password = "P5hfBvTW";
// const char* ssid = "RADIUSAD549";
// #define WIFI_PASSWORD "NL9yXrsS9G"

#define API_KEY "AIzaSyBRG8rQ5IARNBay2SdQvbMJ9a52TkvDrwQ"
#define DATABASE_URL "https://hydrogrowth-420be-default-rtdb.firebaseio.com/"

FirebaseData firebaseData;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
const char* host = "api.pushingbox.com";

#define PhSensorPin 34
#define WaterLevelSensorPin A3
#define TdsSensorPin A0
// const int sensorPin = A0;

int waterLevelSensorValue = 0;

#define VREF 5.0           // analog reference voltage(Volt) of the ADC
#define SCOUNT 30          // sum of sample point
int analogBuffer[SCOUNT];  // store the analog value in the array, read from ADC
int analogBufferTemp[SCOUNT];
int analogBufferIndex = 0;
int copyIndex = 0;
float averageVoltage = 0;
float tdsValue = 1000;

float temperature = 25;  // current temperature for compensation


#define ONE_WIRE_BUS 15
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);


const char* ntpServer1 = "pool.ntp.org";
const char* ntpServer2 = "time.nist.gov";
const long gmtOffset_sec = 28800;
const int daylightOffset_sec = 3600;

void timeavailable(struct timeval* t) {
  Serial.println("Got time adjustment from NTP!");
  // printLocalTime();
}

const char* waterLevelDevID = "v8976948E29AE1B7";
const char* tempDevID = "vBED8B59DD467795";
const char* tdsDevID = "v69C1126FAD58BEE";
const char* phID = "v609D44A2A36FB27";


void setup() {
  Serial.begin(115200);

  sntp_set_time_sync_notification_cb(timeavailable);
  sntp_servermode_dhcp(1);
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer1, ntpServer2);

  WiFi.begin(ssid, password);
  Serial.println("Connecting to " + String(ssid));

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("Wi-Fi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  sensors.begin();
  pinMode(TdsSensorPin, INPUT);

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
int count = 0;

void loop() {


  if (waterLevelSensorValue < 800 && waterLevelSensorValue != 0) {
    sendNotification(waterLevelDevID);
  }

  if (calculatepHValue(PhSensorPin) < 5.5) {
    sendNotification(phID);
  }

  if (sensors.getTempCByIndex(0) < 18) {
    sendNotification(tempDevID);
  }




  sensors.requestTemperatures();
  Serial.println("Celsius temperature: " + String(sensors.getTempCByIndex(0)));
  Serial.println("Fahrenheit temperature: " + String(sensors.getTempFByIndex(0)));

  calculateTDSValue();

  waterLevelSensorValue = analogRead(WaterLevelSensorPin);
  Serial.println("Water Level: " + String(waterLevelSensorValue));

  Serial.println("pH Value: " + String(calculatepHValue(PhSensorPin), 2));

  delay(1000);


  Firebase.RTDB.setString(&firebaseData, "sensorValues/ph", String(calculatepHValue(PhSensorPin), 2));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/celsius", String(sensors.getTempCByIndex(0)));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/farenheit", String(sensors.getTempFByIndex(0)));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/tds", String(tdsValue));
  Firebase.RTDB.setString(&firebaseData, "sensorValues/waterLevel", String(waterLevelSensorValue));


  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 30000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();


    delay(5000);
    struct tm timeinfo;
    if (!getLocalTime(&timeinfo)) {
      Serial.println("No time available (yet)");
      // return;
    }

    char timeString[50];                                                           // Define a character array to store the formatted time string
    strftime(timeString, sizeof(timeString), "%A, %B %d %Y %H:%M:%S", &timeinfo);  // Convert timeinfo to a string

    Serial.println(timeString);  // Print the formatted time string

    char timeString2[50];
    // strftime(timeString, sizeof(timeString), "%A%B%d%Y%H:%M:%S", &timeinfo);



    FirebaseJson content;
    String documentPath = "sensorData/" + String(timeString2);  // Modify the document path as needed
    count++;

    content.set("fields/tds/stringValue/", String(tdsValue));
    content.set("fields/waterLevel/stringValue/", String(waterLevelSensorValue));
    content.set("fields/fahrenheit/stringValue/", String(sensors.getTempFByIndex(0)));
    content.set("fields/celsius/stringValue/", String(sensors.getTempCByIndex(0)));
    content.set("fields/ph/stringValue/", String(calculatepHValue(PhSensorPin), 2));
    content.set("fields/timestamp/stringValue", String(timeString));

    Firebase.Firestore.createDocument(&firebaseData, "hydrogrowth-420be", "", documentPath.c_str(), content.raw());
    Serial.printf("ok\n%s\n\n", firebaseData.payload().c_str());
  }
}



void sendNotification(String devid) {
  WiFiClient client;
  const int httpPort = 80;

  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return;
  }

  String url = "/pushingbox?devid=" + String(devid);
  client.print("GET " + url + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n");

  unsigned long timeout = millis();
  while (client.available() == 0) {
    if (millis() - timeout > 5000) {
      Serial.println(">>> Client Timeout !");
      client.stop();
      return;
    }
  }
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

  float phValue = (float(averageValue) * 3.3 / 1024 / 6) * 3.5;
  return phValue;
}

void calculateTDSValue() {
  static unsigned long analogSampleTimepoint = millis();
  if (millis() - analogSampleTimepoint > 40U) {  //every 40 milliseconds,read the analog value from the ADC
    analogSampleTimepoint = millis();
    analogBuffer[analogBufferIndex] = analogRead(TdsSensorPin);  //read the analog value and store into the buffer
    analogBufferIndex++;
    if (analogBufferIndex == SCOUNT) {
      analogBufferIndex = 0;
    }
  }

  static unsigned long printTimepoint = millis();
  if (millis() - printTimepoint > 800U) {
    printTimepoint = millis();
    for (copyIndex = 0; copyIndex < SCOUNT; copyIndex++) {
      analogBufferTemp[copyIndex] = analogBuffer[copyIndex];

      // read the analog value more stable by the median filtering algorithm, and convert to voltage value
      averageVoltage = getMedianNum(analogBufferTemp, SCOUNT) * (float)VREF / 4096.0;

      //temperature compensation formula: fFinalResult(25^C) = fFinalResult(current)/(1.0+0.02*(fTP-25.0));
      float compensationCoefficient = 1.0 + 0.02 * (temperature - 25.0);
      //temperature compensation
      float compensationVoltage = averageVoltage / compensationCoefficient;

      //convert voltage value to tds value
      tdsValue = (133.42 * compensationVoltage * compensationVoltage * compensationVoltage - 255.86 * compensationVoltage * compensationVoltage + 857.39 * compensationVoltage) * 0.5;
      Serial.println("TDS Value: " + String(tdsValue, 2) + " ppm");
    }
  }
}

int getMedianNum(int bArray[], int iFilterLen) {
  int bTab[iFilterLen];
  for (byte i = 0; i < iFilterLen; i++)
    bTab[i] = bArray[i];
  int i, j, bTemp;
  for (j = 0; j < iFilterLen - 1; j++) {
    for (i = 0; i < iFilterLen - j - 1; i++) {
      if (bTab[i] > bTab[i + 1]) {
        bTemp = bTab[i];
        bTab[i] = bTab[i + 1];
        bTab[i + 1] = bTemp;
      }
    }
  }
  if ((iFilterLen & 1) > 0) {
    bTemp = bTab[(iFilterLen - 1) / 2];
  } else {
    bTemp = (bTab[iFilterLen / 2] + bTab[iFilterLen / 2 - 1]) / 2;
  }
  return bTemp;
}
