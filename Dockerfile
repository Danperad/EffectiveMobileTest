FROM gradle:8.9-jdk AS build

COPY build.gradle ./
COPY gradle/ ./gradle/
COPY src/main/ ./src/main/

RUN gradle clean bootJar

FROM eclipse-temurin:21-alpine AS run
ARG JAR_FILE=target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]