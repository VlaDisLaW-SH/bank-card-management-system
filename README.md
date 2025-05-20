# Система Управления Банковскими Картами

Проект предоставляет функции создания, управления и просмотра данных о банковских картах, а также операции, 
связанные с их использованием.

## Технологии

- Java 17
- Spring Boot 3
- Spring Security 6
- JPA (Hibernate)
- Gradle
- PostgreSQL
- Liquibase
- Docker
- OpenAPI (Swagger)

## Требования

- Java 17+
- Gradle 8+
- PostgreSQL 14+
- Liquibase 4+
- Docker

## Установка и запуск локально

### 1. Клонировать репозиторий

```bash
git clone https://github.com/VlaDisLaW-SH/bank-card-management-system.git
cd bank-card-management-system
```
### 2. Безопасность. Создать переменные окружения
```bash
#Для безопасного использования приложения сгенерируйте собственные значения для каждой переменной
echo. > .env
echo CARD_ENCRYPTOR_PASSWORD=1a2b3c4d5e6f7a8b >> .env
echo JWT_SECRET=dGhpcyBpcyBhIHNhbXBsZSBzZWNyZXQga2V5 >> .env
```

### 3. БД. Поднять контейнер Docker с PostgreSQL
```bash
docker pull postgres:latest

docker volume create pg_data

docker run -d --name pg_container -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin
 -e POSTGRES_HOST_AUTH_METHOD=md5 -v pg_data:/var/lib/postgresql/data postgres:latest
```

### 4. Настроить базу данных. Подключиться к БД под админом и выполнить следующие команды
```bash
docker exec -it pg_container psql -U admin -d postgres
```
```postgresql
CREATE DATABASE db_bank;

CREATE SCHEMA bank_schema;
```

### 5. Выполнить миграции
```bash
cd .\bank-card-management-system\src\main\resources\db\changelog
liquibase --url jdbc:postgresql://host.docker.internal:5432/db_bank?currentSchema=bank_schema --username admin --password admin --contexts=dev --changeLogFile changelog-master.xml updateCount 4
```

### 6. Сборка проекта. Поднять контейнер Docker с приложением
```bash
cd .\bank-card-management-system
.\gradlew.bat clean build
docker build -t card-management-app .
docker run -p 8080:8080 card-management-app
```

### Полезные команды
- Запуск тестов: 
```bash
./gradlew test
```

### Контакты
- Для вопросов и предложений: vlad.shulikoff@gmail.com
