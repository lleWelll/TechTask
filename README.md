# TechTask

Проект **TechTask** представляет собой REST API сервис для управления задачами (CRUD для сущности Task), реализованный с использованием Spring Boot. Приложение упаковано в Docker-контейнер и подключено к базе данных PostgreSQL с миграциями.

## Функциональность

- **Создание задачи** (`POST api/v1/tasks`)
- **Получение задачи по ID** (`GET api/v1/tasks/{id}`)
- **Получение всех задач** (`GET api/v1/tasks`)
- **Обновление задачи** (`PUT api/v1/tasks/{id}`)
- **Удаление задачи** (`DELETE api/v1/tasks/{id}`)
- **Валидация входных данных**
- **Глобальная обработка ошибок** через `@ControllerAdvice`
- **Swagger UI** для удобного тестирования API

## Технологии
- Java 17
- Spring Boot
- Lombok
- MapStruct
- Spring Data JPA
- PostgreSQL
- Liquibase (миграции БД)
- Docker
- Swagger (OpenAPI)
- JUnit / Mockito

## Требования к запуску
- Java 17+
- Docker
- PostgreSQL

## Запуск с помощью Docker (пошагово)
1. Скопируйте проект на локальную машину `git clone https://github.com/lleWelll/TechTask.git`
2. Перейдите в директорию с проектом (там где лежит файл Dockerfile) и соберите Docker образ проекта `docker build -t img_name .`
3. Запустите образ `docker run --name app -e DB_URL="postgreURL" -e DB_PASSWORD="password" -p 8080:8080 img_name`

### Для запуска **обязательно** необходимо указать 2 переменные окружения:
- `DB_URL` (`url` адрес для связи с запущенным PostgreSQL), например `jdbc:postgresql://host.docker.internal:5432/technical`, ссылка должна вести на созданную схему.
- `DB_PASSWORD` (Пароль для пользователя БД, если не указан пользователь, то применяется к мастер пользователю `postgres`)

### **Опциональные** переменные:
- `DB_USER` (указывает пользоватля для БД, по умолчанию указывает на мастер пользователя `postgres`)

## Миграции базы данных
Миграции реализованы с помощью `Liquibase`. Скрипты находятся в `src/main/resources/db/changelog/`.
При первом запуске контейнера они автоматически применятся.

## Swagger UI
Документация и тестирование API доступны по адресу:

```http://localhost:8080/swagger-ui.html```

## Примеры запросов
**Создание Task**:
```
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "NewTask",
    "description": "Description",
    "status" "PENDING"
  }'
```
Ответ:
```
HTTP/1.1 201 Created
{
    "id": "1c446be4-a128-4ba6-9b17-6fb342872008",
    "title": "NewTask",
    "description": "Description",
    "status" "PENDING",
    "createdAt": "2025-06-19T20:19:34.475452",
    "updatedAt": "2025-06-19T20:19:34.475498"
}
```
**Удаление Task**:
```
curl -X DELETE http://localhost:8080/api/v1/tasks/1c446be4-a128-4ba6-9b17-6fb342872008
```
Ответ:
```
HTTP/1.1 204 No Content
```
**Получение Task**:
```
curl http://localhost:8080/api/v1/tasks/1c446be4-a128-4ba6-9b17-6fb342872008
```
Ответ:
```
HTTP/1.1 200 OK
{
    "id": "1c446be4-a128-4ba6-9b17-6fb342872008",
    "title": "NewTask",
    "description": "Description",
    "status" "PENDING",
    "createdAt": "2025-06-19T20:19:34.475452",
    "updatedAt": "2025-06-19T20:19:34.475498"
}
```
**Получение всех Task**:
```
curl http://localhost:8080/api/v1/tasks
```
Ответ:
```
HTTP/1.1 200 OK
[
  {
    "id": "1c446be4-a128-4ba6-9b17-6fb342872008",
    "title": "NewTask",
    "description": "Description",
    "status" "PENDING",
    "createdAt": "2025-06-19T20:19:34.475452",
    "updatedAt": "2025-06-19T20:19:34.475498"
  }
]
```
**Обновление Tasks**:
```
curl -X PUT http://localhost:8080/api/v1/tasks/1c446be4-a128-4ba6-9b17-6fb342872008 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "This is new Title",
  }'
```
Ответ:
```
HTTP/1.1 200 OK
{
    "id": "1c446be4-a128-4ba6-9b17-6fb342872008",
    "title": "This is new Title",
    "description": "Description",
    "status" "PENDING",
    "createdAt": "2025-06-19T20:19:34.475452",
    "updatedAt": "2025-06-19T20:19:34.475498"
}
```

## Демонстрация:

