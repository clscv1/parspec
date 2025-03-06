# Use Maven image to build the JAR
FROM maven:3.8.3-openjdk-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# Use a smaller OpenJDK runtime for the final image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/Parspec-1.0-SNAPSHOT.jar order-service.jar

# Expose the application port
EXPOSE 4567

# Run the application
CMD ["java", "-jar", "order-service.jar"]
