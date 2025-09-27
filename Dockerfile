# Multi-stage build for Spring Boot app
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Leverage layer caching: copy build files first
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Copy source and build
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# Runtime image
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy built jar
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Environment/config
ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
