FROM openjdk:17
WORKDIR /app
COPY build/libs/ChallengeProject-0.0.1-SNAPSHOT.jar challengeProject.jar
ENTRYPOINT ["java", "-jar", "challengeProject.jar"]
