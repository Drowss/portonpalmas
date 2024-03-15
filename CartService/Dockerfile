FROM openjdk:21-slim
COPY target/CartService-0.0.1-SNAPSHOT.jar cart-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "cart-app.jar"]