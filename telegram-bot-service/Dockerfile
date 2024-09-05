FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/telegram-bot-service-0.0.1-SNAPSHOT.jar /app/telegram-bot-service.jar
ENTRYPOINT ["java", "-jar", "telegram-bot-service.jar"]
