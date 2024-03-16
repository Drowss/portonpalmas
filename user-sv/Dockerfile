FROM openjdk:21-slim
COPY target/user-sv-0.0.1-SNAPSHOT.jar user-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "user-app.jar"]