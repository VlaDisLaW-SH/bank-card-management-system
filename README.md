# Система Управления Банковскими Картами

Проект предоставляет функции создания, управления и просмотра данных о банковских картах, а также операции, 
связанные с их использованием.

## Технологии

- Java 17
- Spring Boot 3
- Spring Security 6
- Gradle
- PostgreSQL
- Docker

## Требования

- Java 17+
- Gradle 8+
- PostgreSQL 14+
- Docker

## Установка и запуск локально

### 1. Клонировать репозиторий

```bash
git clone https://github.com/VlaDisLaW-SH/bank-card-management-system.git
cd bank-card-management-system
```

### 2. Настроить Docker
```bash
docker pull postgres:latest

docker volume create pg_data

docker run -d --name pg_container -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin
 -e POSTGRES_HOST_AUTH_METHOD=md5 -v pg_data:/var/lib/postgresql/data postgres:latest
```

### 3. Настроить базу данных
```postgresql
CREATE DATABASE db_bank;

CREATE SCHEMA bank_schema;
```

### 4. Собрать проект
```bash
./gradlew build
```
### 5. Запустить приложение
```bash
./gradlew bootRun
```
### Полезные команды
- Запуск тестов: 
```bash
./gradlew test
```

### Контакты
- Для вопросов и предложений: vlad.shulikoff@gmail.com
