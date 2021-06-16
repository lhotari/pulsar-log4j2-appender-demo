# Pulsar Log4j2 appender demo with Spring Boot

### Starting local Pulsar standalone

```bash
docker run --name pulsar-standalone -d -p 8080:8080 -p 6650:6650 apachepulsar/pulsar:2.8.0 /pulsar/bin/pulsar standalone
```

stopping and removing
```
docker stop pulsar-standalone
docker rm pulsar-standalone
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
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --server.port=8082 --spring.application.name=app1 &
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --server.port=8083 --spring.application.name=app2 &
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

### k8s demo

Results in this type of log events:
```json
{
  "instant" : {
    "epochSecond" : 1623755791,
    "nanoOfSecond" : 493000000
  },
  "thread" : "main",
  "level" : "INFO",
  "loggerName" : "com.github.lhotari.pulsarlogging.demo.DemoApplication",
  "message" : "Started DemoApplication in 1.91 seconds (JVM running for 3.162)",
  "endOfBatch" : false,
  "loggerFqcn" : "org.apache.commons.logging.LogAdapter$Log4jLog",
  "threadId" : 1,
  "threadPriority" : 5,
  "applicationName" : "Demo Application",
  "javaVersion" : "Java version 1.8.0_292",
  "kubernetes.serviceAccountName" : "default",
  "kubernetes.containerId" : "containerd://16be42624b73dd314f8d700ea674ba65efaede65c9eff9b1c12a5523310f9a3c",
  "kubernetes.containerName" : "app",
  "kubernetes.host" : "pop-os",
  "kubernetes.labels.app" : "pulsar-log4j2-appender-demo",
  "kubernetes.labels.pod-template-hash" : "c749d5cf7",
  "kubernetes.master_url" : "https://172.30.183.1:443/",
  "kubernetes.namespaceId" : "8bfb0845-707a-49d1-91f3-7b1ac389718a",
  "kubernetes.namespaceName" : "pulsar-testenv",
  "kubernetes.podID" : "c6cb18b7-dd89-42c9-98ae-52311f94eaa5",
  "kubernetes.podIP" : "172.17.179.98",
  "kubernetes.podName" : "pulsar-log4j2-appender-demo-c749d5cf7-vrzn4",
  "kubernetes.imageId" : "localhost:32000/pulsar-log4j2-appender-demo/app@sha256:95103f481b7f1752623b7afdbb87e8810ab8d5a860b2e87ad79e48ceb27bf592",
  "kubernetes.imageName" : "localhost:32000/pulsar-log4j2-appender-demo/app:latest"
}
```

Pushing to local microk8s docker image repository
```bash
./gradlew bootBuildImage --imageName localhost:32000/pulsar-log4j2-appender-demo/app:latest
docker push localhost:32000/pulsar-log4j2-appender-demo/app:latest
```

deploying
```bash
kubectl -n pulsar-testenv apply -f kube/pulsar-log4j2-appender-demo.yaml
```

checking results
```bash
kubectl exec -i -t -n pulsar-testenv pulsar-testenv-deployment-broker-0 -c pulsar-testenv-deployment-broker -- /pulsar/bin/pulsar-client consume -s sub -p Earliest -n 0 persistent://public/default/logging-demo
```
