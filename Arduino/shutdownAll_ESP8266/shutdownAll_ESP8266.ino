#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

#define ssid  "*****"                               //Ssid network
#define pass  "******"                              //password network
#define _hostname  "****"                           // hostname

bool isUsedIP(IPAddress ip);                        //Method for check is save up or don't
void writer(IPAddress ip, int port, char* msg);     // Method for create and write UDP packet

IPAddress saveIP[1000];                             // save ip
int savePort[1000];                                 // save port
int count = 0;                                      //count for save

const IPAddress ip();               //Static ip example 192.168.1.<you number>
const IPAddress gateway();                          //ip router  example 192.168.1.1
const IPAddress subnet();                           // mask network   example 255 255 255 0

WiFiUDP udp;  // UDP object for chating

char incomingPacket[256];  //Buffer

void setup() {
  Serial.begin(115200);                              // set serial speed
  WiFi.mode(WIFI_STA);                               // Set station mode
  WiFi.begin(ssid, pass);                            // Connect to WiFi
  WiFi.config(ip, gateway, subnet);                  // set static ip
  WiFi.hostname(_hostname);                          //set hostname
  while (WiFi.status() != WL_CONNECTED) delay(500);  //wait connect
  udp.begin(45000);                                  // UDP server begin at 45000 port
}

void loop() {
  int sizePacket = udp.parsePacket();
  if (sizePacket) {
    int len = udp.read(incomingPacket, 256);
    if (len > 0) {
      incomingPacket[len] = 0;
    }
    if ((String)(incomingPacket) == "start" && count < 1000) {
      //check is save ip or don't
      if (!isUsedIP(udp.remoteIP() , udp.remotePort())) {
        saveIP[count] = udp.remoteIP();
        savePort[count] = udp.remotePort();
        writer(saveIP[count], savePort[count], "ok");
        count++;
      } else {
        writer(udp.remoteIP(), udp.remotePort(), "ok");
      }
    } else if ((String)(incomingPacket) == "iscon" && isUsedIP(udp.remoteIP() , udp.remotePort())) {
      writer(udp.remoteIP(), udp.remotePort(), incomingPacket);
    } 
  }
  if (Serial.parseInt() == 1  && count > 0) {
    for (int i = 0; i < count; i++) {
      writer(saveIP[i], savePort[i], "shutdown");
    }
    count = 0;
  }
}

bool isUsedIP(IPAddress ip , int port) {
  for (int i = 0; i < count; i++) {
    if (saveIP[i] == ip && savePort[i] == port )  {
      return true;
    }
  }
  return false;
}

void writer(IPAddress ip, int port, char* msg) {
  udp.beginPacket(ip, port);
  udp.write(msg);
  udp.endPacket();
}
