# Proposal: testcontainers-postgres

Order: 5 of 13. Depends on: `runtime-startup` (Flyway at startup).

## Why

No test runs against a real database. `AuctionItemsTest` is disabled (`baseline-build`), and the repository queries, the SQL triggers and the migrations are only exercised by hand. The later changes depend on PostgreSQL semantics that mocks can't show:
- conditional `UPDATE`s;
- `FOR UPDATE SKIP LOCKED`;
- `ON CONFLICT DO NOTHING`;
- savepoints.
They need real-database tests that also run in CI, which has no PostgreSQL service.

## What Changes

- Testcontainers 2 (managed by Boot 4.2) with a PostgreSQL container wired through `@ServiceConnection`, one shared container per test JVM in each data service.
- Integration tests run in the `verify` phase through `maven-failsafe-plugin` (`*IT`). `mvn test` stays fast, and `mvn clean verify` runs both.
- `AuctionItemsTest` is replaced by a real integration test that creates an auction against a migrated database.
- An integration test covers the start job's conditional `UPDATE` from `runtime-startup` against PostgreSQL.
- users-service integration tests set a dummy `EXCHANGE_API_KEY` in a test profile. The full context loads the currency client but never calls it.
- The database image moves from `postgres:13.4-alpine` (PostgreSQL 13 reached end of life in November 2025) to `postgres:17-alpine`, in tests and in `docker-compose.yml`.

Deviations from brief: none. This fulfils the Postgres half of brief item 3; Kafka containers come in `outbox-publisher` and `settlement-consumer`.

## Non-goals

- Kafka containers.
- End-to-end tests across services (`e2e-smoke-secret-scan`).

## Capabilities

### New Capabilities
None. This adds test infrastructure only (`skip_specs: true`).

### Modified Capabilities
None.

## Impact

- auction-service and users-service: test dependencies, new `*IT` classes, a shared container base class, the failsafe plugin in the root pom.
- `docker-compose.yml`: the database image tag.
- CI: needs Docker, which GitHub's `ubuntu-latest` runners provide.
