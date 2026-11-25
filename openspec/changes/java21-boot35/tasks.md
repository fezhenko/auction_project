# Tasks: java21-boot35

## 1. Security behavior safety net (one commit)

- [x] 1.1 Add an api-gateway test for the current access rules: anonymous, USER and ADMIN requests against `/api/v1/auth`, `/api/v1/users/**`, `/api/v1/bids/**` and `/swagger-ui/index.html`, asserting today's allow or deny outcome for each. Verify: the test passes on Boot 2.7.18.
- [x] 1.2 Add an api-gateway test that issues a token through the login flow and gets it accepted by `JwtFilter`. Verify: the test passes.
- [x] 1.3 Verify `./mvnw clean verify` is green on JDK 17, then commit.

## 2. Java 21 and current quality tools on Boot 2.7.18 (one commit)

- [x] 2.1 Set `maven.compiler.release` to 21. Remove the hard-coded `<source>/<target>` 17 from the compiler plugin. Move Lombok to ≥ 1.18.34 and MapStruct to 1.6.3. Verify: `./mvnw -q compile` succeeds on JDK 21. Note from baseline-build: the Boot 2.7.18 BOM manages byte-buddy 1.12.23, which knows class files only up to Java 20, so Mockito mocks will likely fail on JDK 21. Add `net.bytebuddy:byte-buddy` and `byte-buddy-agent` ≥ 1.14 to dependency management here, and drop them in section 3 once Boot 3.5 manages a newer version.
- [x] 2.2 Move to SpotBugs 4.10.x, PMD 7.x through a current `maven-pmd-plugin`, and current Checkstyle through a current `maven-checkstyle-plugin`. Re-check the versions at apply time. Verify: each plugin runs on JDK 21 without a configuration error.
- [x] 2.3 Migrate the rules PMD 7 renamed in `codequality/pmd-ruleset.xml`, keeping the same priorities. Verify: PMD loads the ruleset with no "rule not found" warnings.
- [x] 2.4 Fix every new Checkstyle, PMD and SpotBugs violation in code, without editing thresholds or excluding rules. Verify: the quality plugins pass. Note from baseline-build: Spotless (palantir-java-format) owns layout, and palantir has no layout options. If the newer Checkstyle flags palantir output (for example `Indentation` on lambdas, switch expressions or records), stop and ask the owner whether to align those checks with the formatter. Do not loosen them silently.
- [x] 2.5 Set the CI `java-version` to 21. Verify `./mvnw clean verify` is green on JDK 21, then commit.

## 3. Boot 3.5.16, jakarta, Security 6, springdoc 2 (one commit)

- [x] 3.1 Run OpenRewrite `UpgradeSpringBoot_3_5` once, keeping `javax.xml.bind` for jjwt 0.9.1. Switch the BOMs to Boot 3.5.16 and Spring Cloud 2025.0.x. Verify: `grep -rE 'import javax\.(servlet|validation|annotation)'` over `*/src` returns nothing. Note from section 1: jjwt 0.9.1 needs `javax.xml.bind.DatatypeConverter`, which reaches the gateway today only through springdoc-openapi-ui 1.6.9 → swagger-core 2.2.0 → `jakarta.xml.bind-api` 2.3.3. springdoc 2 drops it, so add `javax.xml.bind:jaxb-api` to the gateway explicitly. Do not pin `jakarta.xml.bind:jakarta.xml.bind-api` to 2.3.3, because Boot 3.5 manages it at 4.x for the `jakarta.*` packages. `JwtFilterLoginFlowTest` fails if this is missing. If Feign moves off Apache HttpClient 4 here, keep `@DirtiesContext` on the gateway `AbstractConfiguration` unless an A/B run shows the stale-connection failure is gone.
- [x] 3.2 Rewrite `SecurityConfig` with the lambda DSL and `requestMatchers`, keeping the same rules. Verify: the access-rule test from 1.1 passes unchanged. Note from section 1: Security 6 authorizes the ERROR dispatch, which Security 5 skipped. A 403 or 404 keeps its status in Tomcat only if the /error dispatch is permitted (for example `dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()`) or the caller is authenticated again there, which `JwtFilter` (a `GenericFilterBean`) does today. The container rows in `SecurityConfigAccessTest` guard this. Do not turn `JwtFilter` into a `OncePerRequestFilter` without them green. Known and out of scope: Boot also registers `JwtFilter` as a servlet filter, so each request looks the user up in users-service twice.
- [x] 3.3 Replace `springdoc-openapi-ui` with `springdoc-openapi-starter-webmvc-ui` 2.x. Verify: a context test confirms `/v3/api-docs` is served in each service.
- [x] 3.4 Verify that the token test from 1.2 passes and `./mvnw clean verify` is green on JDK 21, then commit.
