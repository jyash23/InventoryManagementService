FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${SERVER_PORT:-8080} -Dspring.mongodb.uri=${SPRING_MONGODB_URI:-mongodb://localhost:27017/notification_service} -Dspring.kafka.bootstrap-servers=${SPRING_KAFKA_BOOTSTRAP_SERVERS:-localhost:9092} -Dspring.kafka.consumer.group-id=${SPRING_KAFKA_CONSUMER_GROUP_ID:-notification-service} -Dapp.kafka.topic.order-placed=${APP_KAFKA_TOPIC_ORDER_PLACED:-order.placed} -Deureka.client.serviceUrl.defaultZone=${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:-http://localhost:8761/eureka} -jar /app/app.jar"]
