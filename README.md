# ESP32 Environmental Monitoring System

This project utilizes the ESP32 by Espressif Systems (version 2.0.11) to create an environmental monitoring system. The system integrates various sensors to measure temperature, TDS (Total Dissolved Solids), pH, and water level. Additionally, it utilizes Firebase for data storage and Blynk for real-time monitoring.

## Required Libraries

Make sure to install the following libraries:

1. Dallas Temperature by Miles Burton (version 3.9.0)
2. Firebase Arduino Client Library for ESP8266 and ESP32
3. DS18B20
4. BlynkNcpDriver
5. GravityTDS-master

## Required Drivers

Install the following drivers for proper connectivity:

- CH34x Windows Drivers (version 3.4)
- CP210x Windows Drivers [Download Here](https://www.silabs.com/developers/usb-to-uart-bridge-vcp-drivers?tab=downloads)

## Setup

Before running the code, make the following changes in the code:

```cpp
// Replace these values with your own credentials
#define WIFI_SSID "your_wifi_ssid"
#define WIFI_PASSWORD "your_wifi_password"
#define API_KEY "your_firebase_api_key"
#define DATABASE_URL "your_firebase_database_url"
```

## Wiring Instructions

### TDS Sensor
- GND
- Power VCC (3.3V~5.5V)
- VP

### Temperature Sensor
- Yellow: D15
- Black: GND
- Red: 3.3V
- 500(?) ohm resistor between yellow and red

### pH Sensor
- GND
- Power VCC (3.3V~5.5V)
- A: D34

### Water Level Sensor
- GND
- Power VCC (3.3V~5.5V)
- A: VN

## External Services

Make sure to create accounts and obtain the necessary API keys for the following services:

- [PushingBox](https://www.pushingbox.com/login.php)
- [Pushbullet](https://www.pushbullet.com/#settings)
- [Picasso (for image processing)](https://square.github.io/picasso/)

## Acknowledgments

Special thanks to Espressif Systems, Miles Burton, Firebase, Blynk, and the contributors of the required libraries for making this project possible.
