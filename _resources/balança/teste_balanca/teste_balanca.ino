void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);  
}

void loop() {
  String leitura = "";
  if(Serial.available() > 0){
    leitura = Serial.read();
    delay(100);
    Serial.println(leitura);
  }
  else{
    Serial.println("teste");
  }

  
  delay(100);
}
