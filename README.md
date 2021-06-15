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

### Running multiple applications

```bash
./gradlew bootJar
```

```bash
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --server.port=8082 &
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --server.port=8083 &
```

reproduces the problem
```
2021-06-15 12:22:11,530 main ERROR Failed to start pulsar manager org.apache.pulsar.client.api.PulsarClientException$ProducerBusyException: Producer with name 'pulsar-log4j2-appender-persistent://public/default/logging-demo' is already connected to topic
	at org.apache.pulsar.client.api.PulsarClientException.unwrap(PulsarClientException.java:944)
	at org.apache.pulsar.client.impl.ProducerBuilderImpl.create(ProducerBuilderImpl.java:94)
	at org.apache.pulsar.log4j2.appender.PulsarManager.startup(PulsarManager.java:127)
	at org.apache.pulsar.log4j2.appender.PulsarAppender.start(PulsarAppender.java:187)
	at org.apache.logging.log4j.core.config.AbstractConfiguration.start(AbstractConfiguration.java:303)
	at org.apache.logging.log4j.core.LoggerContext.setConfiguration(LoggerContext.java:627)
	at org.apache.logging.log4j.core.LoggerContext.start(LoggerContext.java:304)
```