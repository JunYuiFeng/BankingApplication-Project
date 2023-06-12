FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
# Set the working directory inside the container
WORKDIR /app

# Copy the entire project to the container's working directory
COPY . .

# Give executable permission to the mvnw script
RUN chmod +x mvnw

# Run the Maven build command
RUN ./mvnw install -U

# Expose the desired port
EXPOSE 8080

# Set the entry point command
ENTRYPOINT ["./mvnw", "spring-boot:run"]