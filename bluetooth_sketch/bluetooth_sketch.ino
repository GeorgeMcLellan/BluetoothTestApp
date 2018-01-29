char c;
void setup() {
  Serial.begin(9600);         //Sets the data rate in bits per second (baud) for serial data transmission
  pinMode(13, OUTPUT);        //Sets digital pin 13 as output pin
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0)
  {
    
    c = Serial.read();
    Serial.print(c);
    Serial.print("\n");
    if (c == 'n')
      digitalWrite(13, HIGH);
    
    else if (c == 'f')
      digitalWrite(13, LOW);
  }
}
