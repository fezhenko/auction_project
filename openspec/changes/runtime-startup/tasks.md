# Tasks: runtime-startup

## 1. Flyway at startup (one commit)

- [ ] 1.1 Add `spring-boot-starter-flyway` and `flyway-database-postgresql` to auction-service and users-service. Delete both `flyway.conf` files and `script/migration.sh`. Verify: against an empty compose database, each service starts and `flyway_schema_history` lists every migration. users-service has the two seeded accounts.
- [ ] 1.2 Update the README "Running locally" steps to drop the manual migration. Verify: following the README on an empty database starts both services.
- [ ] 1.3 Verify `./mvnw clean verify` is green, then commit.

## 2. Runnable layered jars (one commit)

- [ ] 2.1 Add `spring-boot-maven-plugin` (`repackage`) to the three service modules. Verify: `java -Djarmode=tools -jar <module>/target/<module>-*.jar list-layers` prints the layers for each module.
- [ ] 2.2 Verify `./mvnw clean verify` is green, then commit.

## 3. Clock, duration and schedules (one commit)

- [ ] 3.1 Add a `Clock` bean (`Clock.systemUTC()`). Replace `System.currentTimeMillis()` and `LocalDateTime.now()` in scheduling code with the clock. Verify: no direct system-time call remains in `AuctionService` or the scheduler classes (grep).
- [ ] 3.2 Add `auction.duration` (default `PT6H`) and the `auction.scheduler.*` cron and zone properties (zone `UTC`). Remove the `custom.scheduler` keys, except the balance cron, which stays until `settlement-consumer`. Verify: unit tests for `isDue` cover the default 6 hours and a configured 1 minute, at and around the boundary.
- [ ] 3.3 Replace the start job with the single conditional `UPDATE`, taking `:now` from the `Clock`. Verify: a unit test asserts the job passes `Clock.instant()` to the repository. The SQL itself is verified against PostgreSQL in `testcontainers-postgres` task 1.4, because a mock can't check a query.
- [ ] 3.4 Pin `-Duser.timezone=UTC` in the surefire `argLine` and in the README run command. Verify: a test that sets the default zone to `Asia/Tokyo` gets the same `isDue` result.
- [ ] 3.5 Verify `./mvnw clean verify` is green, then commit.
