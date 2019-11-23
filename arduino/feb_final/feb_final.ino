#include "SoftwareSerial.h"


String NETWORK = "LICET";

String PASSWORD = "";


String TS_SERVER = "api.thingspeak.com";

String TS_WRITE_API = "ZODS3DRYUHT2EOZ4";


int fanPin = 4;

int rainVal, febe_statusVal, user_statusVal;


SoftwareSerial ESP8266(2, 3);



void setup() {

Serial.begin(9600);

pinMode(fanPin, OUTPUT);

digitalWrite(fanPin, HIGH);

febe_statusVal = 0;

ESP8266.begin(9600);

ESP8266_RESET();

ESP8266_WIFI();

}



void loop () {

Serial.println("***GET USER STATUS***");

user_statusVal = 0;


if(user_statusVal == 1)
{

  Serial.println("***UNDER USER CONTROL***");

  digitalWrite(fanPin, LOW);

  febe_statusVal = user_statusVal;

  delay(30*1000); // check after 5 minutes
  
}

else {

Serial.println("***UNDER DEVICE'S CONTROL***");

digitalWrite(fanPin, LOW);

febe_statusVal = 1;

Serial.println("***POSTING DATA***");

delay(30*1000); // check after 5 minutes

digitalWrite(fanPin, HIGH);

febe_statusVal = 0;

Serial.println("***POSTING DATA***");

delay(30*1000); // check after 5 minutes

}

}



void POST_DATA () {

Serial.println("***POSTING DATA***");

ESP8266.println("AT+CIPSTART=\"TCP\",\"" + TS_SERVER + "\",80");

delay(1000);

if(ESP8266.find("OK"))
  Serial.println("Thingspeak TCP Connection Ready!");

delay(1000);

String DATA = "field1=" + String(febe_statusVal);

String postRequest =

"POST /update HTTP/1.1\r\nHost: " + TS_SERVER + "\r\n" +

"Connection: close" + "\r\n" +

"X-THINGSPEAKAPIKEY: " + TS_WRITE_API + "\r\n" +

"Content-Type: application/x-www-form-urlencoded\r\n" +

"Content-Length: " + DATA.length() + "\r\n" +

"\r\n" + DATA;

String sendCmd = "AT+CIPSEND=";

ESP8266.print(sendCmd);

delay(1000);

ESP8266.println(postRequest.length());

delay(3000);

if(ESP8266.find(">")) {
  
  Serial.println("Sending to Thingspeak ..."); 
  
  ESP8266.print(postRequest);

  if(ESP8266.find("SEND OK"))  
    Serial.println("Data Sent to Thingspeak!");

}

ESP8266.println("AT+CIPCLOSE");

}



void GET_USER_STATUS () {

Serial.println("***GET USER STATUS***");

ESP8266.println("AT+CIPSTART=\"TCP\",\"" + TS_SERVER + "\",80");

delay(1000);

if(ESP8266.find("OK"))
  Serial.println("Thingspeak TCP Connection Ready!");

delay(1000);

String postRequest =
"GET /channels/222785/fields/2/last/?api_key=CLOK5XPN5J5N7EBF HTTP/1.1\r\nHost: api.thingspeak.com\r\n\r\n";

String sendCmd = "AT+CIPSEND=";

ESP8266.print(sendCmd);

delay(1000);

ESP8266.println(postRequest.length());

delay(3000);

if(ESP8266.find(">")) { 
  
  Serial.println("Getting from Thingspeak ..."); 
  
  ESP8266.print(postRequest);

  delay(1000);

if(ESP8266.find("SEND OK")) { 
  
  Serial.print("User Status : ");
  
  delay(5000);
  
  user_statusVal = 0; //checked as once the value is set, exit
  
  while(ESP8266.available() && user_statusVal == -1) {

    String response = " " + ESP8266.readStringUntil('\r');

    if(response.indexOf("2412") >= 0)
      user_statusVal = 0;

    else if(response.indexOf("2512") >= 0)
      user_statusVal = 1;

    else
      user_statusVal = 0;

  }

if(user_statusVal == 0)
  Serial.println("OFF");

else if(user_statusVal == 1)
  Serial.println("ON");

}
}

ESP8266.println("AT+CIPCLOSE");

}



void ESP8266_RESET() {

ESP8266.println("AT+RST");

delay(5000);

if(ESP8266.find("ready")) 
  Serial.println("Module Reset!");

else {
  
  Serial.println("Module Not Reset!");
  //ESP8266_RESET();

}
}



void ESP8266_WIFI() {

String cmd = "AT+CWJAP=\"" +NETWORK+"\",\"" + PASSWORD + "\"";

ESP8266.println(cmd);

delay(5000);

if(ESP8266.find("OK"))
  Serial.println("Connected to Wifi!");

else {
  
  Serial.println("Not Connected to Wifi!");
  //ESP8266_WIFI();
  
}
}
