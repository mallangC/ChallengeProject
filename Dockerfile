# 1단계: 베이스 이미지 설정 (Ubuntu 기반 사용)
FROM ubuntu:20.04

# 2단계: JDK 설치
RUN apt-get update && apt-get install -y openjdk-17-jdk wget curl

# 3단계: dockerize 설치
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz \
    && tar -xvzf dockerize-linux-amd64-v0.6.1.tar.gz \
    && mv dockerize /usr/local/bin/

# 4단계: JAR 파일 경로 설정
ARG JAR_FILE=build/libs/*.jar

# 5단계: JAR 파일을 컨테이너에 복사
COPY ${JAR_FILE} app.jar


