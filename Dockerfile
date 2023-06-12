FROM ubuntu:latest AS build
RUN apt-get update
# Stage 1: Build the Maven project
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY . /app
RUN mvn clean install -U

# Stage 2: Create the final Docker image
FROM openjdk:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]