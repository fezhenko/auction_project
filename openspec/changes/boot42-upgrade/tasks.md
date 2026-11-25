# Tasks: boot42-upgrade

## 1. JSON shape safety net (one commit)

- [x] 1.1 Add JSON round-trip tests for every request and response DTO used by gateway controllers and by the Feign clients, pinning field names and the date format. Verify: the tests pass on Boot 3.5.16.
- [x] 1.2 Verify `./mvnw clean verify` is green, then commit.

## 2. WireMock without Spring Cloud Contract (one commit)

- [x] 2.1 Replace `spring-cloud-contract-wiremock` with `org.wiremock.integrations:wiremock-spring-boot` (`@EnableWireMock`) in the gateway tests, still on Boot 3.5.16, so the Boot 4.0 commit doesn't also change the test infrastructure. Spring Cloud 2025.1.5 and 2026.0.0 no longer manage Spring Cloud Contract, whose last release is 5.0.3. The stub bodies move to `wiremock/__files` on the test classpath, and the server's base URL is published straight into both `services.*.url` properties. Verify: every gateway test passes on Boot 3.5.16, the access-rule test unchanged.
- [x] 2.2 Verify `./mvnw clean verify` is green, then commit.

## 3. Boot 4.0.x through OpenRewrite (one commit)

- [x] 3.1 Run `org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0` once, and pin Boot 4.0.x with Spring Cloud 2025.1.x. Add a root `lombok.config` with `lombok.jacksonized.jacksonVersion += 3`. Verify: `./mvnw -q compile` succeeds with no Lombok Jackson-version warnings. Note from java21-boot35: the root `lombok.config` already exists with `lombok.jacksonized.jacksonVersion += 2`, because jackson-annotations 2.20+ is shared by Jackson 2 and 3; add `+= 3` to it. Per the Boot 4.0 migration guide, also add `spring-boot-properties-migrator` (runtime) for this step and remove it before the commit. The guide's modular starters replace `spring-boot-starter-web` with `spring-boot-starter-webmvc` and want the technology test starters (`spring-boot-starter-webmvc-test`, `spring-boot-starter-security-test` and so on) instead of `spring-boot-starter-test`.
- [x] 3.2 Update the Security 7 configuration so the access-rule test from `java21-boot35` passes unchanged. Move springdoc to the 3.x line for Boot 4.0. Verify: the access-rule test, the JSON tests from 1.1 and a `/v3/api-docs` context test pass. Note from java21-boot35: Boot 4 no longer provides `TestRestTemplate` from `@SpringBootTest`. The container rows in `SecurityConfigAccessTest` need `@AutoConfigureTestRestTemplate` and the test-scoped `spring-boot-resttestclient`; put the annotation on the gateway `AbstractConfiguration` so the test itself stays unchanged. The service `OpenApiDocsTest` slices import springdoc's auto-configuration classes by name; check those names on springdoc 3. `@MockBean` is already replaced by `@MockitoBean`. Owner decisions (2026-11-25): Boot 4 moved `TestRestTemplate` to `org.springframework.boot.resttestclient`, so the access-rule test may change its import line and nothing else; keep the absolute login redirect that Security 7 replaced with a relative one by default.
- [x] 3.3 Verify `./mvnw clean verify` is green, then commit.

## 4. Boot 4.2.0 and Spring Cloud 2026.0.0 (one commit)

- [x] 4.1 Bump to Boot 4.2.0, Spring Cloud 2026.0.0 and springdoc 3.2.x. Apply the 4.1 property renames and the 4.2 release-note changes. Switch Testcontainers coordinates to the 2.x artifact names in dependency management. Verify: `./mvnw dependency:tree` shows `spring-boot` 4.2.0 and `spring-cloud-openfeign-core` 5.1.0. Note from baseline-build: delete the root Testcontainers entries and the `testcontainers.version` property instead of renaming them. Explicit entries would override the Testcontainers 2.x that Boot manages. springdoc 3.2 uses swagger-core 3.0.0, which publishes the annotations as `io.swagger.core.v3:swagger-annotations` without the `-jakarta` suffix. Note from implementation: swagger-core 3 no longer brings Jackson 2 databind, so `lombok.config` keeps only `jacksonVersion += 3`; OpenRewrite's `SpringBootProperties_4_1` found nothing to rename.
- [x] 4.2 Verify `./mvnw clean verify` is green, then commit. If it is blocked by a 4.2.0 regression, apply the 4.1.3 / 2025.1.5 fallback and note it for the PR description.

## 5. RestTestClient in the access-rule test (one commit)

- [x] 5.1 Replace `TestRestTemplate`, deprecated for removal in Boot 4.2, with `RestTestClient` in the container rows of `SecurityConfigAccessTest`. `AbstractConfiguration.tomcat()` binds it to the server port with a JDK `HttpClient` that never follows redirects; the client from `@AutoConfigureRestTestClient` can't take a request factory, and its default one follows redirects. This is an owner-approved exception (2026-11-25) to "the access-rule test passes unchanged": the `@CsvSource` row tables stay byte-identical, and the client's redirect policy is set explicitly. Verify: the access-rule test passes, and `git diff` shows no change to its rows.
- [x] 5.2 Verify `./mvnw clean verify` is green, then commit.

## 6. JWT signing key (one commit)

- [x] 6.1 Replace jjwt 0.9.1 with jjwt 0.13.x (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`). Build the key from `JWT_SECRET` as UTF-8 bytes. Fail startup when it is missing or shorter than 32 bytes, with a message that names the variable but not its value. Verify these tests:
  - a context test where a 31-byte secret fails startup;
  - a context test where a 32-byte secret starts;
  - a check that the failure output doesn't contain the secret;
  - the login-and-call token test passes;
  - a token signed with another key is rejected.

  Note from implementation: `application.yaml` reads `${JWT_SECRET:}`, so a missing variable reaches the same check and message as a short one. `JwtProperties.signingKey()` runs the check from the `Jwt` constructor, not during binding, because a binding failure report would print the value. The startup tests start the whole gateway and check that Tomcat never started.
- [x] 6.2 Update the README variable table: `JWT_SECRET` is used as raw UTF-8, at least 32 bytes, and tokens from older versions are invalid. Verify: the README text matches the startup check.
- [x] 6.3 Verify `./mvnw clean verify` is green, then commit.

## 7. Actuator exposure (one commit)

- [x] 7.1 Set `management.endpoints.web.exposure.include: health,info`, `endpoint.health.show-details: never`, `show-components: never` and `endpoint.health.probes.enabled: false` in all three services. Boot 4 turns the liveness and readiness probes on by default; the owner decided (2026-11-25) to keep them off, as on Boot 3.5. Verify: for auction-service and users-service, a test where `/actuator/health` returns 200 with the body exactly `{"status":"UP"}`, `/actuator/info` returns 200, and `/actuator/env` returns 404. Note from implementation: the service tests run Boot's auto-configuration with the service's `application.yaml` and none of its beans, with the database health check and the JDBC repositories off, so they need no PostgreSQL. `testcontainers-postgres` adds tests against a real database. The gateway also gets a test over real HTTP: anonymous health gives exactly `{"status":"UP"}`, and `env`, `beans`, `configprops` and `heapdump` give 404 even to an ADMIN.
- [x] 7.2 Permit anonymous `GET /actuator/health` and `/actuator/info` in the gateway security chain. Verify:
  - the access-rule test (from `java21-boot35`) gains rows: anonymous health → 200, anonymous info → 200, anonymous `GET /api/v1/auctions` → rejected;
  - gateway tests where `/actuator/env` and `/actuator/heapdump` with an ADMIN token → 404.
- [x] 7.3 Update the README sentence that says the services "expose all actuator endpoints" and the tech-stack versions. Verify: the README matches the configuration.
- [x] 7.4 Verify `./mvnw clean verify` is green, then commit.
