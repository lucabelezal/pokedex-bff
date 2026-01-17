# Dockerfile para o Pokedex BFF (Spring Boot Kotlin)
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o JAR gerado pelo build do Gradle
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
