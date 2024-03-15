FROM openjdk:21-slim
COPY target/productsv-0.0.1-SNAPSHOT.jar product-app.jar
COPY src/main/resources/application.properties application.properties
ENTRYPOINT ["java", "-jar", "product-app.jar"]