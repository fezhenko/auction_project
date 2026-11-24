# Design: boot42-upgrade

## Context

After `java21-boot35`, the build is green on Java 21 with Boot 3.5.16, jakarta, Security 6 and springdoc 2. The gateway signs tokens with jjwt 0.9.1 through `signWith(SignatureAlgorithm.HS256, jwtSecret)`. Given a `String`, that call base64-decodes it, and it accepts any length. 79 DTO files use Lombok `@Jacksonized`. auction-service and users-service set `management.endpoints.web.exposure.include: '*'`. The gateway has no `management` configuration (Boot's default exposes only `health`), but its security chain ends in `anyRequest().authenticated()` with form login, so an anonymous `/actuator/health` is redirected or rejected.

Versions checked on 2026-11-24:

| Line | GA | Open-source support until | Notes |
|---|---|---|---|
| Boot 4.2.0 | 2026-11-19 | 2027-12-31 | Spring Cloud 2026.0.0 (its BOM pins Boot 4.2.0) and springdoc 3.2.1 (built against 4.2.0). It manages Kafka 4.4.0, spring-kafka 4.2.0 and Testcontainers 2.0.5 |
| Boot 4.1.3 | 2026-11-19 | 2027-07-31 | Spring Cloud 2025.1.5 supports 4.1.x |

## Goals / Non-Goals

**Goals:**
- Green on Boot 4.2.0 and Spring Cloud 2026.0.0, with unchanged HTTP routes, access rules and JSON field names.
- The behavior in `specs/gateway-authentication` and `specs/service-operations`.

**Non-Goals:**
- Using new Boot 4 features beyond what the upgrade requires.

## Decisions

1. **Boot 4.2.0 over 4.1.3.** It has five more months of open-source support, and every dependency already supports it. The risk is that both 4.2.0 and Spring Cloud 2026.0.0 are `.0` releases. The fallback is to set `spring-boot.version` to 4.1.3 and the Spring Cloud train to 2025.1.5. That's a change to one property each, and the PR description records it if used.
2. **Two commits through 4.0.** OpenRewrite open source has `UpgradeSpringBoot_4_0` but no complete 4.1 or 4.2 recipe.
   - Commit A: run the recipe, then pin Boot 4.0.x with Spring Cloud 2025.1.x and get it green.
   - Commit B: bump to 4.2.0 and Spring Cloud 2026.0.0, and apply the `boot-41-properties` recipe plus the 4.2 release notes by hand.
3. **Jackson 3 through Lombok config.** Add a root `lombok.config` with `lombok.jacksonized.jacksonVersion += 3`. Lombok 1.18.48, managed by Boot 4.2.0, otherwise generates Jackson 2 annotations and only warns. JSON round-trip tests pin the current field names, and the date format of `auctionDate` and `createdAt`, before the switch. That catches Jackson 3 default changes, such as dates as ISO strings versus timestamps. If a default changes the output, configure the old format explicitly instead of changing the API.
4. **jjwt 0.13.x with a raw UTF-8 key.** `Keys.hmacShaKeyFor(secret.getBytes(UTF_8))` rejects keys under 256 bits. Validation runs in a `@ConfigurationProperties` class with a startup check, so a bad key fails fast with a message that names the variable and never shows its value.
   - Rejected: keep base64 decoding. It hides the real key length from the operator and was never documented.
5. **Actuator exposure** is set per service in `application.yaml`: `include: health,info`, `endpoint.health.show-details: never`, `show-components: never`. On the gateway, the security chain adds `requestMatchers(GET, "/actuator/health", "/actuator/info").permitAll()` before `anyRequest().authenticated()`. Container healthchecks (`docker-compose-app`) and the E2E `--wait` depend on that.

## Risks / Trade-offs

- [A `.0` regression in Boot 4.2.0 or Spring Cloud 2026.0.0] → Use the fallback in Decision 1. CI stays the gate.
- [Jackson 3 changes the JSON output the round-trip tests don't cover] → The tests cover every DTO returned by gateway controllers, not just one sample.
- [Changing what `JWT_SECRET` means invalidates existing tokens] → Acceptable, since there's no production. The README and the PR description state it.
- [Spring Cloud OpenFeign is feature-complete upstream] → Keep it, because the brief names Spring Cloud. A move to HTTP interface clients is a possible follow-up.

## Migration Plan

Operators set `JWT_SECRET` to a value of at least 32 UTF-8 bytes. Rollback means reverting the commits and restoring the old secret.
