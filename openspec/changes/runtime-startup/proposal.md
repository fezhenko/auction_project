# Proposal: runtime-startup

Order: 4 of 12. Depends on: `boot42-upgrade`.

## Why

The services can't start against a fresh database without a manual step: Flyway runs only through `flyway-maven-plugin` via `script/migration.sh`. No module builds a runnable jar, because there is no `spring-boot-maven-plugin`, and the Docker images need one. Auction timing can't be tested or configured:
- The finish duration is hard-coded to 6 hours (21600000 ms).
- The jobs read `System.currentTimeMillis()`.
- The scheduler zone is `ECT`, a deprecated three-letter alias.
- The start job loads every auction with `findAll()`.

An end-to-end test can't run in reasonable time against fixed six-hour auctions.

## What Changes

- Flyway runs at application startup in auction-service and users-service. `flyway.conf` and `script/migration.sh` are deleted.
- `spring-boot-maven-plugin` builds a layered, runnable jar per service.
- A `java.time.Clock` bean (system UTC) is the only time source for scheduling decisions.
- The auction duration becomes the property `auction.duration` (ISO-8601, default `PT6H`).
- The start and finish job schedules are cron properties, with the same defaults as today, evaluated in UTC instead of `ECT`.
- The start job becomes one conditional update: PLANNED auctions whose start time has passed move to IN_PROGRESS. It no longer loads every auction.
- The README run steps lose the manual migration step.

Deviations from brief: none. This implements the architect's T4 in slimmed form; see Non-goals.

## Non-goals

- Converting `TIMESTAMP` columns to `timestamptz` and `Date` to `Instant` in DTOs. That's deferred and listed as a known limitation. Instead, the JVM time zone is pinned to UTC wherever the services run.
- Rewriting the finish job. `outbox-publisher` replaces it with a conditional update that writes to the outbox.
- The balance job, which `settlement-consumer` deletes.

## Capabilities

### New Capabilities
- `database-migrations`: how each data service brings its schema up to date.
- `auction-scheduling`: when auctions start and when they become eligible to finish.

### Modified Capabilities
None.

## Impact

- auction-service and users-service: poms, `application.yaml`, scheduler classes, `AuctionService.startAuction` and the finish eligibility check.
- Files deleted: `script/migration.sh`, both `flyway.conf` files.
- README "Running locally" section.
