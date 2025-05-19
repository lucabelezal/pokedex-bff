# Build stage (compatível com ARM)
FROM --platform=linux/arm64 eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Dev stage (com hot-reload para M1)
FROM --platform=linux/arm64 eclipse-temurin:17-jdk-jammy as dev
WORKDIR /app
COPY --from=builder /app/gradlew .
COPY --from=builder /app/gradle ./gradle
COPY --from=builder /app/build.gradle.kts .
COPY --from=builder /app/settings.gradle.kts .
COPY --from=builder /app/src ./src
# Instala dependências para hot-reload (ARM-compatible)
RUN apt-get update && apt-get install -y git
ENTRYPOINT ["./gradlew", "bootRun", "--continuous", "--build-cache"]

# Prod stage (opcional)
FROM --platform=linux/arm64 eclipse-temurin:17-jre-jammy as prod
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]