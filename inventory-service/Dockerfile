FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src src
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/inventory-service-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${SERVER_PORT:-8080} -Dspring.datasource.url=${SPRING_DATASOURCE_URL:-jdbc:mysql://localhost:3306/inventory_service} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME:-root} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD:-root} -Dspring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:-update} -Deureka.client.serviceUrl.defaultZone=${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:-http://localhost:8761/eureka} -jar /app/app.jar"]
