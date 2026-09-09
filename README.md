# 📦 task-service

Основной бэкенд-сервис платформы доставки заказов. Хранит партнёров и заказы, отслеживает связанные с заказами GitHub-репозитории и публикует события об изменениях в Kafka.

> Часть платформы из двух сервисов. Второй репозиторий — **[bot-service](https://github.com/<org>/bot-service)**: отказоустойчивый REST-прокси и Telegram-бот поверх этого сервиса.

## Как это работает вместе

```
Telegram ──▶ bot-service ──REST(resilient)──▶ task-service ──▶ PostgreSQL
                  ▲                                 │
                  │                                 ▼
                  └──────── Kafka (order.link.changed) ◀── планировщик трекинга
                                                             GitHub-репозиториев
```

1. Клиент (REST или Telegram) создаёт заказ в `bot-service`, указывая ссылку на GitHub-репозиторий.
2. `bot-service` резилиентно проксирует запрос сюда, в `task-service`, где заказ сохраняется в PostgreSQL.
3. Фоновый планировщик здесь периодически опрашивает GitHub API по всем заказам и сравнивает состояние репозитория со снапшотом.
4. При обнаружении изменений событие пишется в транзакционный outbox и асинхронно публикуется в Kafka-топик `order.link.changed`.
5. `bot-service` потребляет это событие как консьюмер того же топика.

## Что делает сервис

- **CRUD по заказам и партнёрам** — REST API поверх PostgreSQL (партнёр → много заказов, `ON DELETE CASCADE` на уровне JPA).
- **Привязка заказа к GitHub-репозиторию** — при создании заказа указывается ссылка вида `https://github.com/owner/repo`, валидируемая regex'ом как на уровне DTO, так и на уровне `GitHubClient`.
- **Фоновый трекинг репозиториев** — планировщик каждый час (настраивается) опрашивает GitHub API по всем заказам, сравнивает снапшот репозитория (дата обновления, название, хэши списков issues/PR) с сохранённым, и при расхождениях фиксирует изменения.
- **Transactional Outbox → Kafka** — обнаруженные изменения кладутся в таблицу `tracking_outbox` в той же транзакции, что и обновление снапшота, а отдельный планировщик каждые 10 секунд вычитывает outbox и публикует события в топик `order.link.changed`, удаляя запись только после успешной отправки.
- **Отказоустойчивость GitHub API** — Resilience4j: rate limiter (10 запросов/сек), retry с экспоненциальным backoff, circuit breaker с фолбэком, различающим бизнес-ошибки (невалидная ссылка, 4xx) от временной недоступности.
- **Наблюдаемость** — Actuator + Micrometer/Prometheus на отдельном management-порту, готовый дашборд Grafana (RED-метрики) в `docker/grafana`.
- **Документация API** — Swagger UI/OpenAPI через springdoc.

## Технологический стек

| Категория | Технологии |
|---|---|
| Язык / платформа | Java 25, Spring Boot 4.0.6 |
| Web | Spring Web MVC, springdoc-openapi (Swagger UI) |
| Данные | Spring Data JPA **и** Spring Data JDBC (переключаемые реализации), PostgreSQL, Liquibase-миграции |
| Асинхронность | Apache Kafka (`spring-kafka`), Transactional Outbox |
| Отказоустойчивость | Resilience4j (CircuitBreaker, RateLimiter, Retry), Spring AOP |
| Наблюдаемость | Spring Boot Actuator, Micrometer + Prometheus, Grafana |
| Тестирование | JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL) |
| Прочее | Lombok, Spotless (palantir-java-format) |

## Архитектура и ключевые модули

```
controller/   REST-контроллеры (Orders, Partners) + OpenAPI-интерфейсы (*Api)
service/      Бизнес-логика (OrderService, PartnerService)
repository/   Абстракция доступа к данным с двумя реализациями:
              repository/jpaRepository — на Spring Data JPA
              repository/jdbcRepository — на JdbcTemplate
              выбор реализации — через свойство `repository.type: jpa|jdbc`
entity/       JPA-сущности: OrderEntity, PartnerEntity, RepoSnapshotEntity, TrackingOutboxEntity
mapper/       Ручные мапперы Entity <-> DTO
dto/          Request/Response модели с bean-валидацией
github/       Клиент GitHub REST API (RestClient) + DTO ответов
tracking/     Логика снапшотов репозитория, диффа, outbox-паттерна и планировщиков
exception/    Доменные исключения + GlobalExceptionHandler (RestControllerAdvice)
validator/    Дополнительная бизнес-валидация запросов
config/       Бины: выбор repository-реализации, GitHub RestClient, OpenAPI, ExecutorService трекинга
```

### Двойная реализация репозиториев

Интересная архитектурная особенность: `OrderRepository`/`PartnerRepository` — интерфейсы с двумя взаимозаменяемыми имплементациями (JPA и чистый JDBC), выбираемыми через `@ConditionalOnProperty(repository.type)`. Это позволяет сравнивать производительность/поведение обоих подходов без изменения бизнес-кода.

### Domain-модель

- `PartnerEntity` 1—N `OrderEntity` (каскадное удаление, `orphanRemoval`).
- `RepoSnapshotEntity` — последнее известное состояние GitHub-репозитория заказа (имя, `updatedAt`, хэши issues/PR).
- `TrackingOutboxEntity` — очередь недоставленных событий об изменениях (транзакционный outbox).

## API

Базовый путь: `/orders`, `/partners`. Полная спецификация — Swagger UI.

**Orders**
- `GET /orders`, `GET /orders/{id}`
- `POST /orders`, `PUT /orders/{id}`, `DELETE /orders/{id}`

**Partners**
- `POST /partners`, `GET /partners`, `GET /partners/{id}`
- `GET /partners/{id}/orders` — заказы партнёра
- `DELETE /partners/{id}` — каскадно удаляет заказы

## Запуск локально

### 1. Инфраструктура (Postgres, Kafka, Prometheus, Grafana)

```bash
cp .env.example .env   # задать POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD
docker compose up -d
```

| Сервис | Порт |
|---|---|
| PostgreSQL | 7070 → 5432 |
| Kafka | 9092 |
| Prometheus | 9090 |
| Grafana | 3000 (admin/admin) |

### 2. Приложение

```bash
./mvnw spring-boot:run
```

По умолчанию приложение слушает `8081` (REST API) и `8082` (Actuator/Prometheus). Liquibase применит миграции автоматически при старте.

### Переменные окружения

| Переменная | Назначение | По умолчанию |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | Адрес брокера Kafka | `localhost:9092` |
| `GITHUB_API_TOKEN` | Токен для GitHub API (снижает лимиты) | пусто |
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Параметры БД | см. `.env.example` |

### Основные настраиваемые параметры (`application.yaml`)

- `repository.type` — `jpa` или `jdbc`
- `tracking.repo-check-rate-ms` — период опроса GitHub (по умолчанию 1 час)
- `tracking.outbox.dispatch-rate-ms` / `batch-size` / `topic` — параметры диспетчера outbox
- `resilience4j.*` — тюнинг circuit breaker / rate limiter / retry для GitHub API (отдельный, более агрессивный профиль — в `application-resilience.yaml`)

## Тестирование

```bash
./mvnw test
```

Покрытие включает unit-тесты (валидаторы, мапперы, resilience-сценарии GitHub-клиента через WireMock-подобные подходы), компонентные тесты сервисов для обеих реализаций репозитория (JPA/JDBC) и интеграционные тесты трекинга репозиториев — с поднятием реальной PostgreSQL через Testcontainers.

## Мониторинг

`docker/prometheus/prometheus.yml` собирает метрики с management-порта сервиса; `docker/grafana` содержит готовый provisioning и дашборд `task-service-red.json` с RED-метриками (Rate/Errors/Duration) для HTTP-эндпоинтов.