FROM openjdk:21-slim
COPY target/config-server-0.0.1-SNAPSHOT.jar config-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "config-app.jar"]