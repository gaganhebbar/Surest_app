# ================================
# 🟣 Stage 1 — Build the project
# ================================
FROM gradle:8.10-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy Gradle wrapper & build files first (better caching)
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build Spring Boot JAR
RUN ./gradlew bootJar --no-daemon


# ================================
# 🟢 Stage 2 — Run the JAR
# ================================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
