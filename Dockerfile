FROM eclipse-temurin:17-jre
WORKDIR /app
COPY betty-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
