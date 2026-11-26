# auction_project

Online auction platform built as three Spring Boot microservices behind a JWT-secured API gateway.

An ADMIN creates accounts, and users log in at `POST /api/v1/auth` to get a JWT. The intended flow: sellers put items up for auction, buyers place bids, and scheduled jobs start auctions, finish them, mark the item as sold and settle the balances. Most of that flow does not work yet; see [Known limitations](#known-limitations).

## Architecture

```mermaid
flowchart LR
    client([Client]) -->|REST + JWT| gw[api-gateway :8080]
    gw -->|OpenFeign| auc[auction-service :8081]
    gw -->|OpenFeign| usr[users-service :8082]
    auc -->|OpenFeign: item status, balances| usr
    usr -->|OpenFeign| fx[apilayer Exchange Rates API]
    auc --> adb[(PostgreSQL: auctions)]
    usr --> udb[(PostgreSQL: users)]
```

| Service | Port | Responsibility |
|---|---|---|
| `api-gateway` | 8080 | Public REST API under `/api/v1`. Issues and validates JWTs, hashes passwords with BCrypt, keeps sessions stateless and enforces ADMIN / USER roles. Forwards calls to the other services through OpenFeign clients. |
| `auction-service` | 8081 | Auctions, bids, buyers and sellers. A scheduled job starts `PLANNED` auctions once their start date has passed. The finish job tries to finish `IN_PROGRESS` auctions once `auction.duration` (6 hours by default) has passed since their start date and mark the item `SOLD`, but the failing item call rolls the finish back. A third job requests balance settlement, which currently changes no balances (see [Known limitations](#known-limitations)). |
| `users-service` | 8082 | Users and balances, payments, items and categories, and currencies with conversion through the apilayer Exchange Rates Data API. |

Each data service owns its own PostgreSQL database. Their schemas are versioned with Flyway, each service applies its pending migrations at startup, and database triggers keep current prices, buyers and sellers in sync with new bids and auctions.

`auction-service` and `users-service` have no authentication of their own. They rely on running in an isolated network, reachable only through the gateway. Every service, the gateway included, exposes only the `health` and `info` actuator endpoints, and `health` reports only the overall status. The gateway answers those two without a token, so container healthchecks can call them.

## Tech stack

- Java 21, Spring Boot 4.2, Spring Security 7, Spring Data JDBC, Spring Cloud 2026.0 (OpenFeign), Jackson 3
- PostgreSQL 13, Flyway
- JWT (jjwt 0.13, HMAC-SHA256), BCrypt
- MapStruct, Lombok, springdoc-openapi (Swagger UI)
- JUnit 6, Mockito, Spring Boot Test, WireMock
- Checkstyle, PMD and SpotBugs, which fail the Maven build on violations
- Spotless with palantir-java-format, which fails the build on unformatted Java
- Docker Compose for the databases, plus a GitHub Actions workflow that checks formatting and runs `./mvnw clean verify` on Temurin 21

## Running locally

Requirements: JDK 21, Docker, and `curl` or `wget` plus `unzip` for the Maven Wrapper (`./mvnw` downloads Maven 3.9.16).

1. Start the databases. users-service gets port 15432 and auction-service gets port 15433.

   ```bash
   docker compose up -d
   ```

2. Provide the secrets as environment variables. They are never committed.

   | Variable | Used by | Purpose |
   |---|---|---|
   | `JWT_SECRET` | api-gateway | Secret that signs and validates JWTs with HMAC-SHA256. Its UTF-8 bytes are the key as they are, so it must be at least 32 bytes long; without it, or with a shorter one, the gateway doesn't start. Earlier versions base64-decoded it, so tokens they issued are no longer valid. |
   | `EXCHANGE_API_KEY` | users-service | API key for the [apilayer Exchange Rates Data API](https://apilayer.com/marketplace/exchangerates_data-api). Only the currency convert and symbols endpoints call it, so any non-empty value lets the service start. |

3. Build the project, then start each service in its own terminal:

   ```bash
   ./mvnw clean install -DskipTests
   java -Duser.timezone=UTC -jar users-service/target/users-service-1.0-SNAPSHOT.jar
   java -Duser.timezone=UTC -jar auction-service/target/auction-service-1.0-SNAPSHOT.jar
   java -Duser.timezone=UTC -jar api-gateway/target/api-gateway-1.0-SNAPSHOT.jar
   ```

   If you run `UsersApplication`, `AuctionApplication` and `ApiGatewayApplication` from your IDE instead, add the VM option `-Duser.timezone=UTC`. The `TIMESTAMP` columns store UTC wall-clock times, and another JVM time zone shifts auction start times.

   On startup, users-service and auction-service apply their pending Flyway migrations, so an empty database needs no manual step. A service whose migration fails, or whose applied migration no longer matches its file, doesn't start. The users-service migrations seed an ADMIN account (`test@admin.com`) and a USER account (`test@user.com`) for local development.

Each service serves Swagger UI at `/swagger-ui/index.html`. On the gateway, that path requires an ADMIN JWT.

## Development

- Format before every commit: `./mvnw spotless:apply`. Unformatted Java fails `./mvnw verify` at `validate`, locally and in CI.
- Optional pre-commit hook, once per clone: `git config core.hooksPath .githooks`. It formats your staged Java files and stops the commit if it changed any of them, so you can review and `git add` the result.
- `git config blame.ignoreRevsFile .git-blame-ignore-revs` makes local `git blame` skip the one-time reformat. GitHub does this automatically.

## Known limitations

The fixes are planned as OpenSpec changes in [`openspec/changes/`](openspec/changes/).

- Creating an auction through the gateway fails. auction-service's `POST /api/v1/sellers` returns no body, so the gateway reads a null seller and answers "Auction creation fails, please try again later". Fixed in [`auction-lifecycle-fixes`](openspec/changes/auction-lifecycle-fixes/proposal.md).
- The gateway can't set an auction's start date, so auctions created through it never start. Only auction-service itself has `PATCH /api/v1/auctions/{id}/date`. Fixed in [`auction-lifecycle-fixes`](openspec/changes/auction-lifecycle-fixes/proposal.md).
- Joining and bidding through the gateway fail. Joining throws before the first bid, because the self-join check reads the auction's leading buyer, which is still empty. A successful bid still answers "balance is not enough", because the gateway's bid path has no `return` after placing it. Fixed in [`auction-lifecycle-fixes`](openspec/changes/auction-lifecycle-fixes/proposal.md).
- Finishing an auction rolls back. Inside its database transaction, the finish job asks users-service to mark the item `SOLD`. That request carries no price, users-service writes `NULL` into the `NOT NULL` `items.price` column and fails, and the whole finish is rolled back. Fixed in [`outbox-publisher`](openspec/changes/outbox-publisher/proposal.md), which takes the call out of the transaction, and [`settlement-consumer`](openspec/changes/settlement-consumer/proposal.md), which marks the item sold.
- Balance settlement does nothing. auction-service sends the user type as `BUYER` / `SELLER`, users-service expects `buyer` / `seller`, so it logs a warning and leaves the balance unchanged, and the auction is marked as paid anyway. The balance update also runs in a read-only transaction. Fixed in [`settlement-consumer`](openspec/changes/settlement-consumer/proposal.md), which moves settlement to Kafka events.
- `AuctionItemsTest` is disabled: it can't resolve its constructor parameters, and it needs a running PostgreSQL database. [`testcontainers-postgres`](openspec/changes/testcontainers-postgres/proposal.md) replaces it with a self-contained integration test.

## Roadmap

- Replace the balance-settlement Feign call with Kafka events
- Integration tests with Testcontainers
- Dockerfiles and a single-command `docker compose` setup for the whole system
