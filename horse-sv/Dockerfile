FROM openjdk:21-slim
COPY target/horse-sv-0.0.1-SNAPSHOT.jar horse-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "horse-app.jar"]