### Task 4 — Users CRUD (Spring Context + JDBC)
1) Поднять БД:
   docker compose up -d
   # подключение: jdbc:postgresql://localhost:5433/task4db, user=app_user, pass=app_password
2) Сборка/запуск:
   cd task_4
   mvn -DskipTests package
   mvn exec:java
3) Что делает:
    - создает 2х пользователей (alice, bob), читает, обновляет, удаляет, показывает всех.
4) Схема:
   app_data.users (id BIGSERIAL PK, username VARCHAR(255) UNIQUE NOT NULL)
