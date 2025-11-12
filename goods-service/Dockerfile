# Build stage
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

# Copy project files
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY gradlew /app/gradlew
COPY gradlew.bat /app/gradlew.bat
COPY gradle /app/gradle
COPY src /app/src

# Build Spring Boot fat JAR
RUN ./gradlew clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 4569
ENTRYPOINT ["java", "-jar", "/app/app.jar"]