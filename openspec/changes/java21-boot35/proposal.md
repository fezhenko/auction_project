# Proposal: java21-boot35

Order: 2 of 14. Depends on: `baseline-build`.

## Why

Moving from Java 17 to 21, `javax` to `jakarta`, and Spring Security 5 to 6 is the largest mechanical step of the upgrade. Doing it on Spring Boot 3.5.16, the last 3.x release, keeps it apart from Boot 4's changes (Jackson 3, Security 7, modular starters). OpenRewrite's Boot 4.0 recipe also expects a 3.5 starting point. Boot 3.5's open-source support ended on 2026-06-30, so this state is a stepping-stone commit on the branch and never a release target.

## What Changes

- Java 21 (`maven.compiler.release` 21). CI moves to Temurin 21.
- Lombok and MapStruct move to versions that support Java 21: Lombok as managed by the BOM (≥ 1.18.34), MapStruct 1.6.3.
- Quality tools move to versions that can analyze Java 21 bytecode: SpotBugs 4.10.x, PMD 7.x and current Checkstyle. Rules that PMD 7 renamed are migrated in `codequality/pmd-ruleset.xml`. Violations are fixed in code.
- Spring Boot 3.5.16 BOM and Spring Cloud 2025.0.x, the train that supports Boot 3.5.
- `javax.servlet`, `javax.validation` and `javax.annotation` move to `jakarta.*` through OpenRewrite.
- Spring Security 6: `antMatchers` becomes `requestMatchers` in the lambda DSL, with the same URL rules and roles.
- `springdoc-openapi-ui` 1.6.9 is replaced by `springdoc-openapi-starter-webmvc-ui` 2.x.

Deviations from brief: the brief named Boot 3.x as the target. Here 3.5 is only an intermediate commit; the target is Boot 4.2 (`boot42-upgrade`, decision C).

## Non-goals

- No Boot 4, Jackson 3 or jjwt change.
- No change to HTTP routes, security rules or JSON shapes, apart from trailing-slash matching and error bodies (below).

## Known behavior changes

Accepted by the owner on 2026-11-25. All come from new defaults in Spring Boot 3 and Spring Framework 6.

- Spring Framework 6 no longer matches a trailing slash, so `GET /api/v1/auctions/` returns 404 in all three services. No Feign client or test uses a trailing slash.
- Log timestamps use the ISO-8601 default, `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`.
- Actuator `/env` and `/configprops` mask every value.
- api-gateway and auction-service answer framework errors with an RFC 9457 ProblemDetail body (`type`, `title`, `status`, `detail`, `instance`), because their exception handlers extend `ResponseEntityExceptionHandler`. Malformed JSON (400), a wrong method (405) and a wrong media type (415) used to return an empty body. An unknown path (404) used to return Boot's `timestamp`/`status`/`error`/`path` JSON; Spring 6.1 now raises `NoResourceFoundException` for it, which the handlers answer. Status codes don't change. users-service and Spring Security's 403 keep their bodies.
- Feign keeps a pooled Apache HttpClient, now version 5 through `feign-hc5`, because Spring Cloud OpenFeign 4 dropped HttpClient 4. Without it, Feign would fall back to the JDK client.

## Capabilities

### New Capabilities
None. This is a platform migration with no change to observable behavior (`skip_specs: true`).

### Modified Capabilities
None.

## Impact

- Every module pom and `codequality/pmd-ruleset.xml`.
- About 118 source files, the `javax` imports: validation 102, annotation 11, servlet 5.
- api-gateway `SecurityConfig` and `JwtFilter`.
- `.github/workflows/ci.yml`: the JDK moves to 21.
