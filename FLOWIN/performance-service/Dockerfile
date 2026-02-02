FROM openjdk:21-ea-21-slim

COPY build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-Dspring.profiles.active=prod", "-jar", "app.jar" ]