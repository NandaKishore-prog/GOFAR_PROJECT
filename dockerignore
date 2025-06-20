# Use an OpenJDK base image
FROM eclipse-temurin:21-jdk

# Set work directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the full source
COPY src ./src

# Build the project
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "target/Login_Services-0.0.1-SNAPSHOT.jar"]
# Use Eclipse Temurin JDK 21 base image
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Pre-download dependencies (for faster builds)
RUN ./mvnw dependency:go-offline -B

# Copy the entire source code
COPY src ./src

# Build the Spring Boot application (skip tests for faster deploy)
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot's default port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "target/Login_Services-0.0.1-SNAPSHOT.jar"]
