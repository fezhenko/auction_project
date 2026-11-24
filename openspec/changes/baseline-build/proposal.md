# Proposal: baseline-build

Order: 1 of 12. Depends on: nothing.

## Why

`mvn clean verify` fails on `main`, so CI has never been green and no later change can prove it keeps the build green. Two tests fail:
- `AuctionItemsTest` (auction-service) cannot resolve its constructor parameters, and it also needs a manually migrated PostgreSQL.
- api-gateway `AuctionServiceTest` throws `NoClassDefFoundError: AuthorizationEventPublisher`. The cause is a hand-pinned `spring-security-test` 5.6.0 next to Spring Security 5.7.

This change gets a green, reproducible build on the current Boot 2.7 line first. Then a failure in the upgrade changes can only come from the upgrade.

## What Changes

- Import the `spring-boot-dependencies` 2.7.18 and `spring-cloud-dependencies` 2021.0.9 BOMs. Delete the hand-pinned versions they already manage, including `spring-security-test` 5.6.0, Jackson, Mockito, JUnit, the PostgreSQL driver, Testcontainers, SLF4J and `spring-context`.
- Add the Maven Wrapper in script-only mode, so no jar is committed.
- Resolve the code-quality rule paths from `${maven.multiModuleProjectDirectory}` instead of `../../auction_project/…`, so a clone builds under any folder name.
- `.gitignore`: add `target/`, `.env`, `*.iml`, `.DS_Store`.
- Put `@Disabled` on `AuctionItemsTest`, with a reason that points to the `testcontainers-postgres` change, which replaces it.
- Fix in code the SpotBugs findings that appear once every module reaches the `package` phase.
- CI on Temurin 17 with `cache: maven`, current major versions of the actions, and `./mvnw -B clean verify`. Commits stop using `[skip ci]`.
- README: correct claims the code doesn't support today. Auction creation through the gateway, scheduling a start date and finishing auctions don't work yet; the later changes fix them.

Deviations from brief: none. The brief's "CI on Java 21 Temurin" arrives in `java21-boot35`.

## Non-goals

- No Java, Spring Boot or library upgrades beyond what the 2.7.18 BOMs manage.
- No new tests. `AuctionItemsTest` is replaced in `testcontainers-postgres`.
- No change to runtime behavior.

## Capabilities

### New Capabilities
None. This change covers tooling, CI and docs only (`skip_specs: true`).

### Modified Capabilities
None.

## Impact

- `pom.xml` and all three module poms (dependency management, plugin configuration).
- New files: `.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd`.
- `.gitignore`, `.github/workflows/ci.yml`, `README.md`.
- `auction-service/src/test/.../AuctionItemsTest.java` (disabled only).
- Code touched only where SpotBugs reports a real finding.
