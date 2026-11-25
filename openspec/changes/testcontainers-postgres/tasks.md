# Tasks: testcontainers-postgres

## 1. Integration-test infrastructure and auction-service ITs (one commit)

- [ ] 1.1 Add `maven-failsafe-plugin` (`integration-test` and `verify` goals) to the root pom. Add `spring-boot-testcontainers`, `testcontainers-postgresql` and `testcontainers-junit-jupiter` test dependencies to auction-service. Verify: `./mvnw -pl auction-service verify` runs failsafe. Note from java21-boot35: the root pom still pins Testcontainers 1.17.5 (`testcontainers.version` and three dependencyManagement entries), while the Boot 3.5 BOM brings their transitive modules at 1.21.4, so versions are mixed. Delete the pin and the entries here and give the module dependencies `<scope>test</scope>` themselves, because the root entries are what makes them test-scoped today. The development machine runs Docker 29; check that the Testcontainers version in use supports it.
- [ ] 1.2 Add `AbstractPostgresIT` (a static `PostgreSQLContainer("postgres:17-alpine")` with `@ServiceConnection`). Verify: an IT that loads the context sees `flyway_schema_history` with all migrations applied.
- [ ] 1.3 Delete `AuctionItemsTest` and add `AuctionCreationIT`, which creates an auction for a seller and reads it back with state PLANNED. Verify: the IT passes with no local PostgreSQL.
- [ ] 1.4 Add `StartAuctionJobIT` with a fixed `Clock`. It covers every scenario in `auction-scheduling` "A planned auction starts when its start time is reached" against the real conditional `UPDATE` from `runtime-startup`. Verify: the IT passes.
- [ ] 1.5 Verify `./mvnw clean verify` is green with no local PostgreSQL, then commit.

## 2. users-service ITs (one commit)

- [ ] 2.1 Add the same test dependencies and an `AbstractPostgresIT` to users-service, with a test profile that sets a dummy `EXCHANGE_API_KEY`. Verify: a context-loading IT starts and finds the seeded ADMIN and USER accounts.
- [ ] 2.2 Review the tests under `integration/`. `UsersServiceTest` uses `@MockBean` and needs no database, so it stays a unit test. Only tests that actually hit the database are renamed to `*IT` and moved to the container. Verify: every test passes, none is skipped, and `UsersServiceTest` still runs in the surefire phase.
- [ ] 2.3 Verify `./mvnw clean verify` is green, then commit.

## 3. Compose database image (one commit)

- [ ] 3.1 Change both database services in `docker-compose.yml` to `postgres:17-alpine`. Update the README tech-stack line (PostgreSQL 17) and add the note that `verify` needs Docker. Verify: `docker compose up -d` and both services start against the new image.
- [ ] 3.2 Verify `./mvnw clean verify` is green, then commit. After the push, verify that CI on GitHub runs the ITs (the failsafe report in the job log).
