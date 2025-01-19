FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

COPY database/*.csv ./database/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]