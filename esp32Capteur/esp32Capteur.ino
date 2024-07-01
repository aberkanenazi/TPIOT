#include "BluetoothSerial.h"
#include <WiFi.h>
#include <PubSubClient.h>
#include "DHT.h"
BluetoothSerial SerialBT;
WiFiClient espClient;
PubSubClient client(espClient);
DHT dht(23, DHT22);
long interval = 10000;
unsigned long previousMillis = 0;   // Stores last time temperature was published
String readString; //message bluetooth
float temp;
float hum;
char* mqttUser = "";
char* mqttPassword = "";
String ssid = "";
String password =  "";
char* mqttServer = "xxx.xxx.xxx.xxx";
const int mqttPort = 9001;
int LED_BUILTIN = 2;
int temp_alert = 27;
void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();



  // If a message is received on the topic esp32/output, you check if the message is either "on" or "off". 
  if (String(topic) == "capteur/led") {
    Serial.print("Changing output to ");
    if(messageTemp == "ON"){
      Serial.println("on");
      digitalWrite(LED_BUILTIN, HIGH);
    }
    else if(messageTemp == "OFF"){
      Serial.println("off");
      digitalWrite(LED_BUILTIN, LOW);
    }
  }
}
void setup() {

  Serial.begin(9600);
  dht.begin();//LANCEMENT CAPTEUR
  SerialBT.begin("ESP32TP");
  Serial.println("Program start");
  pinMode (LED_BUILTIN, OUTPUT);
}

void loop() {
  client.loop();
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    Serial.println("OK");
    previousMillis = currentMillis;
    hum = dht.readHumidity();
    temp = dht.readTemperature();
    if (isnan(temp) || isnan(hum)) {
      Serial.println(F("Failed to read from DHT sensor!"));
      return;
    }
    // Publish an MQTT message 
    if (client.connected()) {
      Serial.println("temp");
        if(temp>temp_alert){
          digitalWrite(LED_BUILTIN, HIGH);
        }else{
          digitalWrite(LED_BUILTIN, LOW);
        }
        Serial.println(F("Humidity: "));
        Serial.print(hum);
        Serial.println(F("%  Temperature: "));
        Serial.print(temp);
        client.publish("capteur/temperature",String(temp).c_str());
        client.publish("capteur/humidité", String(hum).c_str());
      
    }

  }

  // Réception d'un message bluetooth
  if (SerialBT.available()) {
    char c = SerialBT.read();
    if (c == '*') {// * = fin du message
      //do stuff
      readString += c;
      int ind1; //les index
      int ind2;
      int ind3;
      ind1 = readString.indexOf(':');  //recherche du séparateur
      String type = readString.substring(0, ind1);   //récupération de la string
      String newinterval;
      String newTempAlert;
      switch (type.toInt()) {
        case 1: {
            Serial.println("WIFI");
            ind2 = readString.indexOf(':', ind1 + 1 ); // deuxieme separateur
            ssid = readString.substring(ind1 + 1, ind2);// ssid
            ind3 = readString.indexOf('*', ind2 + 1);// fin de chaine
            password = readString.substring(ind2 + 1, ind3);// mdp
            // connection au wifi
            int n = ssid.length();
            char char_ssid[n + 1];
            int n2 = password.length();
            char char_password[n2 + 1];
            // string to char array
            strcpy(char_ssid, ssid.c_str());
            strcpy(char_password, password.c_str());
            WiFi.begin(char_ssid, char_password);

            while (WiFi.status() != WL_CONNECTED) {
              delay(500);
              Serial.println("Connecting to WiFi..");
            }
            Serial.println("Connected to the WiFi network");
            client.setServer(mqttServer, mqttPort);
            client.setCallback(callback);
            while (!client.connected()) {
              Serial.println("Connecting to MQTT...");
              if (client.connect("ESP32Client", mqttUser, mqttPassword)) {
                Serial.println("connected");
                client.subscribe("capteur/led");
              } else {
                Serial.print("failed with state ");
                Serial.print(client.state());
                delay(2000);
              }
            }
          }
          break;
        case 2:
          Serial.println("interval change");
          ind2 = readString.indexOf('*', ind1 + 1 ); 
          newinterval = readString.substring(ind1 + 1, ind2);
          interval = newinterval.toInt();
          if (client.connected()) {
            int sendinterval=interval/1000;
            Serial.println("send    ddsds");
            client.publish("capteur/changementInterval", String(sendinterval).c_str());
          }
          break;
         case 3:
          Serial.println("Alert temperature change");
          ind2 = readString.indexOf('*', ind1 + 1 );
          newTempAlert = readString.substring(ind1 + 1, ind2);
          temp_alert = newTempAlert.toInt();
          if (client.connected()) {
            client.publish("capteur/changementTempAlert", String(temp_alert).c_str());
          }
          break; 
      }
      readString = ""; 
    }
    else {
      readString += c; 
    }

  }
  delay(20);
}
