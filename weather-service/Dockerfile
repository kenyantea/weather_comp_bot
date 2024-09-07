FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/weather-service-0.0.1-SNAPSHOT.jar /app/weather-service.jar
ENTRYPOINT ["java", "-jar", "weather-service.jar"]
