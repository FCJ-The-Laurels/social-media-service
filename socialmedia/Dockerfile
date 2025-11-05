# Multi-stage Dockerfile for Spring Boot Reactive MongoDB Application
# Optimized for production with Java 25

# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Download dependencies (cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-jammy

# Install netcat for health checks (optional)
RUN apt-get update && apt-get install -y netcat-traditional && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Expose actuator/prometheus port (same as app)
EXPOSE 8080

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    --add-opens java.base/java.nio=ALL-UNNAMED \
    --add-opens java.base/jdk.internal.misc=ALL-UNNAMED"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD nc -z localhost 8080 || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

