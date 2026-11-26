# Design: runtime-startup

## Context

- Flyway is applied by `flyway-maven-plugin` with a per-module `flyway.conf`, through `script/migration.sh`.
- `auctions.auction_date` and every `created_at` are `TIMESTAMP` (without time zone).
- `AuctionService.startAuction` and `finishAuction` load all auctions and compare against `System.currentTimeMillis()`. The finish rule uses a hard-coded 21600000 ms.
- Schedulers use `zone = "${custom.scheduler.zone}"` = `ECT`, with crons in `custom.scheduler.*`.

## Goals / Non-Goals

**Goals:**
- Services start against an empty database.
- Timing is driven by a `Clock` and properties, so tests can pin time and the end-to-end test can use short auctions.

**Non-Goals:**
- Schema type changes for timestamps (deferred).
- Rewriting finish (`outbox-publisher`).

## Decisions

1. **Flyway through Boot auto-configuration.** Use `spring-boot-starter-flyway` with Flyway 12.x, managed by Boot 4.2, and `flyway-database-postgresql`. Migrations stay in `classpath:db/migration`.
   - Rejected: keep the Maven plugin. It's a manual step and needs database credentials at build time.
2. **Layered jars** through `spring-boot-maven-plugin` (`repackage`, layers enabled by default in Boot 4). `docker-compose-app` extracts them with `-Djarmode=tools`.
3. **One `Clock` bean**, `Clock.systemUTC()`. Tests replace it with `Clock.fixed`. The finish eligibility check moves to a pure function `isDue(startTime, duration, now)` with unit tests, so `outbox-publisher` reuses it.
4. **UTC without a schema change.**
   - The JVM time zone is pinned to UTC wherever the services run: `-Duser.timezone=UTC` in the surefire/failsafe `argLine`, `JAVA_TOOL_OPTIONS` in the Dockerfiles, and the README run command.
   - The cron zone becomes `UTC`.
   - `TIMESTAMP` values are then consistently UTC wall-clock values.
   - Rejected for now: convert the columns to `timestamptz` and the DTOs to `Instant`. It's correct, but it touches every DTO and changes the JSON date output. It's deferred and listed under "Known limitations" in `readme-architecture`.
5. **Start job as one statement:** `UPDATE auctions SET auction_state = 'IN_PROGRESS', last_updated = now() WHERE auction_state = 'PLANNED' AND auction_date <= :now`. It's idempotent and bounded, and needs no `findAll()`.
6. **Property names:**
   - `auction.duration` (Duration)
   - `auction.scheduler.start-cron`
   - `auction.scheduler.finish-cron`
   - `auction.scheduler.zone: UTC`
   The old `custom.scheduler.*` keys are removed. The balance cron stays only until `settlement-consumer` deletes that job.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [A developer runs the service from an IDE without `-Duser.timezone=UTC`, and stored `TIMESTAMP` values shift] → The README run step sets it. The deferred `timestamptz` work removes the dependency.
- [Flyway 12 rejects an old migration (an unsupported statement, or a checksum change)] → The first startup against an empty Testcontainers database runs in CI from `testcontainers-postgres` on. Locally, verify with `docker compose up` before committing.

## Migration Plan

Existing local databases already hold the applied migrations, and Flyway continues from `flyway_schema_history`. Rollback means reverting the commits, after which `script/migration.sh` works again.
