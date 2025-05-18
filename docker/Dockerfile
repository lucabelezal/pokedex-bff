FROM gradle:8.5-jdk17 AS builder
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=/home/gradle/project/build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
