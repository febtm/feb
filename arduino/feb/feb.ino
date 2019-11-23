int fan = 3;
//Licet@#786
void setup() {
  // put your setup code here, to run once:

Serial.begin(9600);

pinMode(fan, OUTPUT);

pinMode(4, INPUT);

digitalWrite(fan, LOW);

}

void loop() {
  // put your main code here, to run repeatedly:

digitalWrite(fan, HIGH);

delay(5000);

digitalWrite(fan, LOW);

delay(5000);

Serial.println(digitalRead(4));

}
