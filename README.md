# auction_project

Online auction platform built as three Spring Boot microservices behind a JWT-secured API gateway.

An ADMIN creates accounts, and users log in at `POST /api/v1/auth` to get a JWT. Sellers put items up for auction, buyers place bids, and scheduled jobs start auctions, finish them and mark the item as sold.

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
| `auction-service` | 8081 | Auctions, bids, buyers and sellers. Scheduled jobs move auctions from `PLANNED` to `IN_PROGRESS` to `FINISHED` and mark the sold item in `users-service`. A third job requests balance settlement for finished auctions (see [Known limitations](#known-limitations)). |
| `users-service` | 8082 | Users and balances, payments, items and categories, and currencies with conversion through the apilayer Exchange Rates Data API. |

Each data service owns its own PostgreSQL database. Their schemas are versioned with Flyway, and database triggers keep current prices, buyers and sellers in sync with new bids and auctions.

`auction-service` and `users-service` have no authentication of their own and expose all actuator endpoints. They rely on running in an isolated network, reachable only through the gateway.

## Tech stack

- Java 17, Spring Boot 2.7, Spring Security, Spring Data JDBC, Spring Cloud OpenFeign
- PostgreSQL 13, Flyway
- JWT (jjwt), BCrypt
- MapStruct, Lombok, springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Spring Boot Test
- Checkstyle, PMD and SpotBugs, which fail the Maven build on violations
- Spotless with palantir-java-format, which fails the build on unformatted Java
- Docker Compose for the databases, plus a GitHub Actions workflow that runs `mvn clean verify`

## Running locally

Requirements: JDK 17, Maven 3.8+, Docker.

1. Start the databases. users-service gets port 15432 and auction-service gets port 15433.

   ```bash
   docker compose up -d
   ```

2. Apply the Flyway migrations for each data service:

   ```bash
   (cd users-service && sh ../script/migration.sh)
   (cd auction-service && sh ../script/migration.sh)
   ```

   The users-service migrations seed an ADMIN account (`test@admin.com`) and a USER account (`test@user.com`) for local development.

3. Provide the secrets as environment variables. They are never committed.

   | Variable | Used by | Purpose |
   |---|---|---|
   | `JWT_SECRET` | api-gateway | HMAC key that signs and validates JWTs |
   | `EXCHANGE_API_KEY` | users-service | API key for the [apilayer Exchange Rates Data API](https://apilayer.com/marketplace/exchangerates_data-api). Only the currency convert and symbols endpoints call it, so any non-empty value lets the service start. |

4. Build the project, then run `UsersApplication`, `AuctionApplication` and `ApiGatewayApplication`, for example from your IDE:

   ```bash
   mvn clean install -DskipTests
   ```

Each service serves Swagger UI at `/swagger-ui/index.html`. On the gateway, that path requires an ADMIN JWT.

## Development

- Format before every commit: `./mvnw spotless:apply`. Unformatted Java fails `./mvnw verify` at `validate`, locally and in CI.
- Optional pre-commit hook, once per clone: `git config core.hooksPath .githooks`. It formats your staged Java files and stops the commit if it changed any of them, so you can review and `git add` the result.
- `git config blame.ignoreRevsFile .git-blame-ignore-revs` makes local `git blame` skip the one-time reformat. GitHub does this automatically.

## Known limitations

- Balance settlement currently does nothing. auction-service sends the user type as `BUYER` / `SELLER`, users-service expects `buyer` / `seller`, and the auction is marked as paid anyway. Settlement moves to Kafka events in the planned rework.
- Two tests fail on the current dependency set: one hits a Spring Security version clash, the other needs a manually migrated database. The upgrade below addresses both.

## Roadmap

- Upgrade to Java 21 and Spring Boot 3
- Replace the balance-settlement Feign call with Kafka events
- Integration tests with Testcontainers
- Dockerfiles and a single-command `docker compose` setup for the whole system
