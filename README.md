# Srping switch

Spring web app to simulate a Lamp switch using the TP-link smart bulb and smart plug. Once the device is on its start to collect the real-time energy consumption info and send to a Kafka topic.

### Deploy

```
mvn clean package spring-boot:repackage
java -jar target/igor-1.0-SNAPSHOT.jar
```

To build and run the native application packaged in a lightweight container:

```
mvn spring-boot:build-image
```

### Build on Raspberry Pi 4(AARCH64)

To install GraalVM on Raspberry PI follow this [link](https://github.com/dongjinleekr/graalvm-ce-deb)

To install Maven follow this [link](https://xianic.net/2015/02/21/installing-maven-on-the-raspberry-pi/)

To set GraalVM as default java version use the following commands:

```
sudo update-alternatives --install /usr/bin/java java /your_path/graalvm-ce-deb/graalvm-ce-java11_aarch64_21.0.0.2/usr/lib/jvm/graalvm-ce-java11/bin/java 1
sudo update-alternatives --config java
```


#Build native image

```
mvn clean install -DskipTests docker:build
```

PS: `GraalVM AARCH64` is under development more details look at [GraalVM Documentation](https://www.graalvm.org/docs/introduction/)


# rum

```
docker run -it igor:0.0.1-SNAPSHOT
```

