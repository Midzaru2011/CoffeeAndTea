# Шаг 1: Используем официальный JDK образ как этап сборки
FROM maven:3.6.3-openjdk-17-slim AS build

# Копируем исходный код в контейнер
COPY . /app
WORKDIR /app

# Собираем приложение (получаем JAR-файл)
RUN mvn clean package -DskipTests && rm -rf ~/.m2

# Шаг 2: Создаем финальный образ с минимальным размером
FROM openjdk:17-jdk-slim

# Устанавливаем Liquibase (если используется)
# RUN apt-get update && apt-get install -y liquibase  # Опционально

# Копируем собранный JAR из первого этапа
COPY --from=build /app/target/CoffeAndTea-0.0.1-SNAPSHOT.jar /CoffeAndTea-0.0.1-SNAPSHOT.jar

# Указываем порт, который будет открыт в контейнере
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/CoffeAndTea-0.0.1-SNAPSHOT.jar"]
