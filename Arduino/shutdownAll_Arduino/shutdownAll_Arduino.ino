//default settings
#define buttonShutdown  2

bool isPressedShutdown = false ,  flagShutdown = false;
int lastTimeShutdown = 0;

void setup() {
  Serial.begin(115200);
  pinMode (buttonShutdown , INPUT_PULLUP);

}

void loop() {
  isPressedShutdown = !digitalRead(buttonShutdown);
  //Buton shutdown
  if (isPressedShutdown  && !flagShutdown && millis() - lastTimeShutdown > 10) {
    flagShutdown = true;
    Serial.println("1");
    lastTimeShutdown = millis();
  } else if (!isPressedShutdown && flagShutdown) {
    flagShutdown = false;
  }
}
