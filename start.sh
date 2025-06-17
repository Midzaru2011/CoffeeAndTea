#!/bin/bash

# Путь к корневой директории проекта
APP_DIR="/home/zag1988/projects/CoffeeAndTea"

# Шаблон имени JAR-файла
JAR_PATTERN="CoffeAndTea-*.jar"

# JVM опции (можно менять по необходимости)
JVM_OPTS="-Xms512m -Xmx2g"

# Перейти в рабочую директорию
cd "$APP_DIR" || { echo "Не могу перейти в $APP_DIR"; exit 1; }

# Найти JAR-файл (берём первый подходящий)
JAR_PATH=$(ls $JAR_PATTERN 2>/dev/null | head -n 1)

# Проверить, найден ли файл
if [ -z "$JAR_PATH" ]; then
  echo "[ERROR] JAR-файл не найден в $APP_DIR" >&2
  exit 1
fi

# Создать папку для логов, если её нет
mkdir -p "$APP_DIR/logs"

# Время запуска
START_TIME=$(date +"%Y-%m-%d %T")

# Логируем старт
echo -e "\e[32m[$START_TIME] Запуск приложения: $JAR_PATH\e[0m" >> "$APP_DIR/logs/app.log"

# Запуск Java-приложения
java $JVM_OPTS -jar "$JAR_PATH" >> "$APP_DIR/logs/app.log" 2>&1 

# Получаем PID запущенного процесса
APP_PID=$!

# Информируем пользователя
echo -e "\e[32m[$START_TIME] Приложение успешно запущено (PID: $APP_PID)\e[0m"