FROM maven:3.9.6-eclipse-temurin-17

# Установка системных зависимостей
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libdbus-1-3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libxdamage1 \
    libxrandr2 \
    libgbm1 \
    libxkbcommon0 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    libatspi2.0-0 \
    xvfb \
    && apt-get clean

# 1. Копируем ВЕСЬ проект (включая модули)
COPY . /app
WORKDIR /app

# 2. Собираем зависимости для всего проекта
RUN mvn dependency:go-offline -B --no-transfer-progress -Dmaven.test.skip=true

# 3. Запуск тестов только для модуля integration-tests
CMD ["mvn", "test", "-B", "-pl", "integration-tests"]