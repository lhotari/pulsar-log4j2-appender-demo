# Pulsar Log4j2 appender demo with Spring Boot

### Starting local Pulsar standalone

```bash
docker run --name pulsar-standalone -d -p 8080:8080 -p 6650:6650 apachepulsar/pulsar:2.7.2 /pulsar/bin/pulsar standalone
```

### Running application

maven
```bash
./mvnw spring-boot:run
```

gradle
```bash
./gradlew bootRun
```

### Consuming events

```bash
docker exec -it pulsar-standalone /pulsar/bin/pulsar-client consume -s sub -p Earliest -n 0 persistent://public/default/logging-demo
```

### Producing log events

Use the `http://localhost:8081/log` endpoint to create log events

```bash
uptime | curl -H 'Content-Type: text/plain' -d @- http://localhost:8081/log
```