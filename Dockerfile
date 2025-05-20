# Estágio de build
FROM gradle:8.7-jdk17-alpine as builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Estágio de produção
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY docker/entrypoint.sh /entrypoint.sh

# Estágio de desenvolvimento
FROM builder as dev
RUN gradle bootJar --no-daemon
CMD ["gradle", "bootRun", "--args='--spring.profiles.active=dev'"]