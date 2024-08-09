# Инструкция по запуску проекта

## Описание

Этот проект представляет собой систему управления задачами, разработанную на Java с использованием Spring Boot и PostgreSQL. Для локального запуска проекта используется Docker Compose.

## Запуск проекта

1. **Клонируйте репозиторий**
   git clone https://github.com/thefbsl/task-management-service.git

2. Сборка и установка зависимостей
   Перед запуском контейнеров Docker необходимо собрать и установить зависимости для модулей task-management-client и task-management-api. Для этого выполните следующие команды:

  cd task-management-client
  mvn clean install

  cd ../task-management-api
  mvn clean install

3. Запуск Docker Compose
   docker-compose up -d --build

    





