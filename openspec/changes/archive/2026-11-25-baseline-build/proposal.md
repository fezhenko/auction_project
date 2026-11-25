# Proposal: baseline-build

Order: 1 of 14. Depends on: nothing.

## Why

`mvn clean verify` fails on `main`, so CI has never been green and no later change can prove it keeps the build green. Two tests fail:
- `AuctionItemsTest` (auction-service) cannot resolve its constructor parameters, and it also needs a manually migrated PostgreSQL.
- api-gateway `AuctionServiceTest` throws `NoClassDefFoundError: AuthorizationEventPublisher`. The cause is a hand-pinned `spring-security-test` 5.6.0 next to Spring Security 5.7. Once that is fixed, the same test calls a live auction-service on `localhost:8081` and fails with `Connection refused`.

This change gets a green, reproducible build on the current Boot 2.7 line first. Then a failure in the upgrade changes can only come from the upgrade.

## What Changes

- Import the `spring-boot-dependencies` 2.7.18 and `spring-cloud-dependencies` 2021.0.9 BOMs. Delete the hand-pinned versions they already manage, including `spring-security-test` 5.6.0, Jackson, Mockito, JUnit, the PostgreSQL driver, Feign, SLF4J and `spring-context`. Testcontainers stays pinned: Boot 2.7 does not manage it.
- Add the Maven Wrapper in script-only mode, so no jar is committed.
- Resolve the code-quality rule paths from `${maven.multiModuleProjectDirectory}` instead of `../../auction_project/…`, so a clone builds under any folder name.
- `.gitignore`: add `target/`, `.env`, `*.iml`, `.DS_Store`.
- Point api-gateway's `AuctionServiceTest` at WireMock (`spring-cloud-contract-wiremock`, BOM-managed) with stubs shaped like auction-service's real responses, so the gateway tests never need a running service.
- Put `@Disabled` on `AuctionItemsTest`, with a reason that points to the `testcontainers-postgres` change, which replaces it.
- Fix the SpotBugs include filter in `codequality/spotbug.xml`. Its categories are ANDed today, so it matches nothing and SpotBugs reports 0 bugs. Then fix in code the findings it reports. This tightens a gate, and the owner approved it.
- Add Spotless with palantir-java-format, matched to the Checkstyle rules. Reformat all Java once in a format-only commit, check formatting in the build and in CI, and add an opt-in git pre-commit hook.
- CI on Temurin 17 with `cache: maven`, current major versions of the actions, a "Check formatting" step, and `./mvnw -B clean verify`. Commits stop using `[skip ci]`.
- README: correct claims the code doesn't support today. Auction creation through the gateway, scheduling a start date and finishing auctions don't work yet; the later changes fix them.

Deviations from brief: the owner added three items during apply, which the brief did not list: the WireMock gateway test, Spotless, and the SpotBugs filter fix. The brief's "CI on Java 21 Temurin" arrives in `java21-boot35`.

## Non-goals

- No Java, Spring Boot or library upgrades beyond what the 2.7.18 BOMs manage.
- No new test classes. `AuctionItemsTest` is replaced in `testcontainers-postgres`; the gateway `AuctionServiceTest` is rewritten in place against WireMock.
- No intended change to runtime behavior. Runtime libraries move to the patch levels the 2.7.18 BOMs manage: Spring 5.3.31, Tomcat 9.0.83, Feign 11.10, Jackson 2.13.5, and the PostgreSQL driver goes back from 42.5.1 to 42.3.8.

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
- api-gateway tests: `AbstractConfiguration`, `AuctionServiceTest`, and a stub body under `src/test/resources/__files/`.
- `codequality/spotbug.xml` (filter corrected, no rule removed), and code touched where SpotBugs then reports a real finding.
- Every Java file is reformatted once by Spotless (format-only commit). New files: `.githooks/pre-commit`, `.git-blame-ignore-revs`.
