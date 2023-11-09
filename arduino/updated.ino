#include <DallasTemperature.h>
#include <OneWire.h>
#include <DS18B20.h>
// temperature libraries

#include <Wire.h>

#define ONE_WIRE_BUS 2
#define SensorPin A1
unsigned long int averageValue;
float b;
int buf[100], temp;

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);


const int analogInPin = A0;
int sensorValue = 0;


void setup(void) {
  Serial.begin(9600);
  sensors.begin();
  pinMode(2, OUTPUT);
  Serial.println("Ready");
}

void loop(void) {

  // Start of printing the water level sensor
  sensorValue = analogRead(analogInPin);
  Serial.println("sensor = " + String(sensorValue));
  delay(2);
  // End  of printing of water level sensor

  // Start of printing the temperatures
  sensors.requestTemperatures();
  Serial.println("Celsius temperature: " + String(sensors.getTempCByIndex(0)));
  Serial.println("Fahrenheit temperature: " + String(sensors.getTempFByIndex(0)));
  delay(3000);
  // End  of printing of water level sensor

  // Start of printing the pH Value
  Serial.println("pH Value: " + String(calculatepHValue(SensorPin), 2));
  // End of printing the pH Value 
  // reference: https://www.youtube.com/watch?v=lIpgGru2Wv0

  digitalWrite(2, HIGH);
  delay(800);
  digitalWrite(2, LOW);
}

float calculatepHValue(int SensorPin) {
  int buf[10];
  int temp;
  float averageValue = 0;

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

  for (int i = 2; i < 8; i++) {
    averageValue += buf[i];
  }

  float phValue = (float(averageValue) * 5.0 / 1024 / 6) * 3.5;
  return phValue;
}
