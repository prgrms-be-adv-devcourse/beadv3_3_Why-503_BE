FROM openjdk:21-jdk-slim

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]