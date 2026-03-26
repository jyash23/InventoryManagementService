FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/discovery-server-1.0-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8761
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
