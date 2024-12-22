# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:8.11.1-jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 시 전달받은 환경 변수 정의
# GitHub Actions에서 전달된 빌드 변수들을 ARG로 정의
ARG JWT_SECRET
ARG RDS_PRIVATE_IP
ARG RDS_PORT
ARG DB_SCHEMA_NAME
ARG DB_USERNAME
ARG DB_PASSWORD
ARG GMAIL_PWD
ARG RIOT_API

# 라이브러리 설치에 필요한 파일만 복사
COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle clean build --no-daemon

# 런타임 이미지로 OpenJDK 17-jre 이미지 지정
FROM eclipse-temurin:17-jre

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/gamegoov2.jar

#EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "gamegoov2.jar"]


