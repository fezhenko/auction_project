# Proposal: boot42-upgrade

Order: 3 of 13. Depends on: `java21-boot35`.

## Why

Spring Boot 3.5 is out of open-source support, and 4.0's support ends on 2026-12-31. Boot 4.2.0 and Spring Cloud 2026.0.0 went GA on 2026-11-19. They are the newest supported line (open-source support until 2027-12-31), and every library this project uses already ships against them. The gateway still uses jjwt 0.9.1, which is unmaintained and accepts signing keys of any length. auction-service and users-service expose every actuator endpoint without authentication. The gateway's actuator endpoints sit behind login, so an anonymous health check (the one container orchestration uses) can't reach them.

## What Changes

- Spring Boot 4.2.0 and Spring Cloud 2026.0.0 (OpenFeign 5.1.0), with springdoc 3.2.x. The path is an intermediate commit on Boot 4.0.x through OpenRewrite `UpgradeSpringBoot_4_0`, then the bump to 4.2.
- Jackson 3. `@Jacksonized` builders (79 files) are told to generate Jackson 3 annotations through `lombok.config`. JSON round-trip tests guard every request and response DTO shape.
- Spring Security 7 configuration updates, with the same access rules.
- Testcontainers 2 artifact names in dependency management, used from `testcontainers-postgres` on.
- jjwt 0.13.x. **BREAKING for operators:** the HMAC key becomes the raw UTF-8 bytes of `JWT_SECRET`. It is no longer base64-decoded, and the gateway refuses to start when the key is shorter than 32 bytes.
- Every service exposes only the `health` and `info` actuator endpoints. The gateway allows anonymous `GET` on those two; everything else on the gateway still requires authentication.
- README: the stack versions and the new meaning of `JWT_SECRET`.

Deviations from brief: the brief asked for Spring Boot 3.x and Spring Cloud 2023+. This change targets Boot 4.2 and Spring Cloud 2026.0 (decision C), because 3.x has no open-source support left. The fallback, if a 4.2.0 regression blocks, is Boot 4.1.3 with Spring Cloud 2025.1.5, a change to one property each.

## Non-goals

- Flyway on startup (`runtime-startup`).
- Authentication for auction-service and users-service. They stay internal-only, which `docker-compose-app` enforces.
- Replacing OpenFeign with HTTP interface clients.

## Capabilities

### New Capabilities
- `gateway-authentication`: how the gateway derives and validates its JWT signing key.
- `service-operations`: which operational endpoints each service exposes.

### Modified Capabilities
None. No specs exist yet.

## Impact

- Every module pom, a new root `lombok.config`, and `SecurityConfig`, `Jwt` and `JwtFilter` in api-gateway.
- The `application.yaml` of all three services (actuator exposure).
- Operators must set a `JWT_SECRET` of at least 32 bytes. Tokens issued before the upgrade stop validating.
- README tech stack and environment variable table.
