FROM openjdk:21-slim
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "api-gateway-app.jar"]