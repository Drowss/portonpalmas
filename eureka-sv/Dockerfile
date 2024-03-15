FROM openjdk:21-slim
COPY target/eureka-sv-0.0.1-SNAPSHOT.jar eureka-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "eureka-app.jar"]
