#FROM openjdk:17-jdk-alpine
#
#COPY target/learning-api.jar learning-api.jar
#
#ENTRYPOINT ["java","-jar","/learning-api.jar"]

# Use the official Maven image with Java 17
#FROM maven:3.8.4-openjdk-17
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the pom.xml file and download dependencies
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
## Copy the rest of the project files
#COPY src ./src
#
## Package the application
#RUN mvn clean install
#
## Specify the command to run the application
#CMD ["java", "-jar", "target/learning-api.jar"]


# Use Maven image to build the app
FROM maven:3.9.5-eclipse-temurin-17 AS builder
# Set working directory
WORKDIR /app
# Copy project files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY src src
# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests
# ----------------------
# Use a lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
# Set working directory
WORKDIR /app
# Copy the built jar from builder
COPY --from=builder /app/target/*.jar app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]




