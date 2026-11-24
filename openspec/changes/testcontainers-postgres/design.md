# Design: testcontainers-postgres

## Context

After `runtime-startup`, both data services apply Flyway migrations at startup. The tests are unit tests with Mockito, plus `AuctionItemsTest`, which is disabled. Boot 4.2 manages Testcontainers 2.0.5, whose artifacts are `testcontainers-postgresql` and `testcontainers-junit-jupiter`.

## Goals / Non-Goals

**Goals:**
- Integration tests against real PostgreSQL in CI with no external database.
- One container per test JVM per service, so the suite stays fast.

**Non-Goals:**
- Kafka, and testing two services in one JVM. The two services share classpath roots (`application.yaml` and `db/migration`), so that is ruled out (decision D).

## Decisions

1. **`@ServiceConnection` with a static container in an abstract base class** (`AbstractPostgresIT`) and `@Testcontainers`. Boot wires the datasource. Flyway migrates the container on context start, which exercises `database-migrations` in every run.
   - Rejected: the JDBC URL scheme (`jdbc:tc:`). It hides the container lifecycle and doesn't work with `@ServiceConnection`.
2. **Failsafe for `*IT`.** `mvn test` keeps unit-test speed, and `mvn verify` runs everything. The quality gates still run on every `verify`.
3. **`postgres:17-alpine`** for both the tests and compose, so they agree. PostgreSQL 13 is out of support. 17 is a mature major that Flyway 12 supports.
   - Rejected: 18. It's newer, and its image changes the data-directory layout, which would matter as soon as a compose volume is added. 17 has the longer track record with Flyway and Testcontainers, and this project gains nothing from 18.
4. **Test data isolation.** Each IT cleans up the rows it created, or uses unique ids. No context-wide `@DirtiesContext`, because it restarts the context and is slow.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [Docker isn't available on a developer machine] → `mvn test` still runs the unit tests. The README states that `verify` needs Docker.
- [Container start time adds to CI] → One shared container per JVM. A full run adds about 10–20 s per service.
