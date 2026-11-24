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
- No change to HTTP routes, security rules or JSON shapes.

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
