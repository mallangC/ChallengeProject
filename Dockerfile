# ✅ 1단계: Build stage
FROM gradle:7.6.0-jdk17 AS builder

WORKDIR /app

COPY . .

# gradlew에 실행 권한 부여
RUN chmod +x gradlew

# JAR 빌드 (테스트는 실행하지 않음)
RUN ./gradlew clean bootJar --no-daemon

# ✅ 2단계: Runtime stage
FROM openjdk:17-jdk-slim

# dockerize 설치 (RDS 준비 대기용)
RUN apt-get update && apt-get install -y wget curl \
  && wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz \
  && tar -xvzf dockerize-linux-amd64-v0.6.1.tar.gz \
  && mv dockerize /usr/local/bin/ \
  && rm dockerize-linux-amd64-v0.6.1.tar.gz

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 시 외부에서 프로파일 지정할 수 있도록
ENTRYPOINT ["dockerize", "-wait", "tcp:mychallenge2.cjwqcwey6bdw.ap-northeast-2.rds.amazonaws.com:3306", "-timeout", "120s", "java", "-jar", "app.jar"]

