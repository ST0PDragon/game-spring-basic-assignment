FROM ubuntu:latest
LABEL authors="sprta"

ENTRYPOINT ["top", "-b"]

# syntax=docker/dockerfile:1

# 1. 빌드
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY . .

# Windows 줄바꿈 처리 및 실행 권한 부여
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Gradle 캐시를 재사용하면서 실행 가능한 JAR 생성
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar

# 2. 실행
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 일반 사용자로 실행
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring \
    /workspace/build/libs/*.jar /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]