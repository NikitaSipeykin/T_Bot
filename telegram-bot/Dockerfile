FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY .. .
RUN mvn -pl telegram-bot -am -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/telegram-bot/target/telegram-bot-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
