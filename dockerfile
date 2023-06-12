FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .
RUN ./mvnw clean install -U
EXPOSE 8080
ENTRYPOINT [“./mvnw”,”spring-boot:run”]