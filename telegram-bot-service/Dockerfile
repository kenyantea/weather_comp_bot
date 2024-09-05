# Используем базовый образ Java
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл сборки в контейнер
COPY target/telegram-bot-service-0.0.1-SNAPSHOT.jar /app/telegram-bot-service.jar

# Указываем команду запуска приложения
ENTRYPOINT ["java", "-jar", "telegram-bot-service.jar"]
