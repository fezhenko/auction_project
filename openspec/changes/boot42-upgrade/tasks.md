# Tasks: boot42-upgrade

## 1. JSON shape safety net (one commit)

- [ ] 1.1 Add JSON round-trip tests for every request and response DTO used by gateway controllers and by the Feign clients, pinning field names and the date format. Verify: the tests pass on Boot 3.5.16.
- [ ] 1.2 Verify `./mvnw clean verify` is green, then commit.

## 2. Boot 4.0.x through OpenRewrite (one commit)

- [ ] 2.1 Run `org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0` once, and pin Boot 4.0.x with Spring Cloud 2025.1.x. Add a root `lombok.config` with `lombok.jacksonized.jacksonVersion += 3`. Verify: `./mvnw -q compile` succeeds with no Lombok Jackson-version warnings. Note from java21-boot35: the root `lombok.config` already exists with `lombok.jacksonized.jacksonVersion += 2`, because jackson-annotations 2.20+ is shared by Jackson 2 and 3; add `+= 3` to it. Per the Boot 4.0 migration guide, also add `spring-boot-properties-migrator` (runtime) for this step and remove it before the commit. The guide's modular starters replace `spring-boot-starter-web` with `spring-boot-starter-webmvc` and want the technology test starters (`spring-boot-starter-webmvc-test`, `spring-boot-starter-security-test` and so on) instead of `spring-boot-starter-test`.
- [ ] 2.2 Update the Security 7 configuration so the access-rule test from `java21-boot35` passes unchanged. Move springdoc to the 3.x line for Boot 4.0. Verify: the access-rule test, the JSON tests from 1.1 and a `/v3/api-docs` context test pass. Note from java21-boot35: Boot 4 no longer provides `TestRestTemplate` from `@SpringBootTest`. The container rows in `SecurityConfigAccessTest` need `@AutoConfigureTestRestTemplate` and the test-scoped `spring-boot-resttestclient`; put the annotation on the gateway `AbstractConfiguration` so the test itself stays unchanged. The service `OpenApiDocsTest` slices import springdoc's auto-configuration classes by name; check those names on springdoc 3. `@MockBean` is already replaced by `@MockitoBean`.
- [ ] 2.3 Verify `./mvnw clean verify` is green, then commit.

## 3. Boot 4.2.0 and Spring Cloud 2026.0.0 (one commit)

- [ ] 3.1 Bump to Boot 4.2.0, Spring Cloud 2026.0.0 and springdoc 3.2.x. Apply the 4.1 property renames and the 4.2 release-note changes. Switch Testcontainers coordinates to the 2.x artifact names in dependency management. Verify: `./mvnw dependency:tree` shows `spring-boot` 4.2.0 and `spring-cloud-openfeign-core` 5.1.0. Note from baseline-build: delete the root Testcontainers entries and the `testcontainers.version` property instead of renaming them. Explicit entries would override the Testcontainers 2.x that Boot manages. Also confirm that Spring Cloud 2026.0 still manages `spring-cloud-contract-wiremock`, which the gateway tests use. If it does not, switch to `org.wiremock.integrations:wiremock-spring-boot` (`@EnableWireMock`).
- [ ] 3.2 Verify `./mvnw clean verify` is green, then commit. If it is blocked by a 4.2.0 regression, apply the 4.1.3 / 2025.1.5 fallback and note it for the PR description.

## 4. JWT signing key (one commit)

- [ ] 4.1 Replace jjwt 0.9.1 with jjwt 0.13.x (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`). Build the key from `JWT_SECRET` as UTF-8 bytes. Fail startup when it is missing or shorter than 32 bytes, with a message that names the variable but not its value. Verify these tests:
  - a context test where a 31-byte secret fails startup;
  - a context test where a 32-byte secret starts;
  - a check that the failure output doesn't contain the secret;
  - the login-and-call token test passes;
  - a token signed with another key is rejected.
- [ ] 4.2 Update the README variable table: `JWT_SECRET` is used as raw UTF-8, at least 32 bytes, and tokens from older versions are invalid. Verify: the README text matches the startup check.
- [ ] 4.3 Verify `./mvnw clean verify` is green, then commit.

## 5. Actuator exposure (one commit)

- [ ] 5.1 Set `management.endpoints.web.exposure.include: health,info`, `endpoint.health.show-details: never` and `show-components: never` in all three services. Verify: for auction-service and users-service, a test where `/actuator/health` and `/actuator/info` return 200, `/actuator/env` returns 404, and the health body has no `components`. Note from the Boot 4.0 migration guide: liveness and readiness probes are on by default, so `/actuator/health` also exposes the `liveness` and `readiness` groups; decide whether they stay.
- [ ] 5.2 Permit anonymous `GET /actuator/health` and `/actuator/info` in the gateway security chain. Verify:
  - the access-rule test (from `java21-boot35`) gains rows: anonymous health → 200, anonymous info → 200, anonymous `GET /api/v1/auctions` → rejected;
  - gateway tests where `/actuator/env` and `/actuator/heapdump` with an ADMIN token → 404.
- [ ] 5.3 Update the README sentence that says the services "expose all actuator endpoints" and the tech-stack versions. Verify: the README matches the configuration.
- [ ] 5.4 Verify `./mvnw clean verify` is green, then commit.
