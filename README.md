# Srping switch

Spring web app to simulate a Lamp switch using the TP-link smart bulb and smart plug. Once the device is on its start to collect the real-time energy consumption info and send to a Kafka topic.


```
mvn clean package spring-boot:repackage
java -jar target/igor-1.0-SNAPSHOT.jar
```