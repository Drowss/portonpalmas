FROM openjdk:21-slim
COPY target/sale-sv-0.0.1-SNAPSHOT.jar sale-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "sale-app.jar"]