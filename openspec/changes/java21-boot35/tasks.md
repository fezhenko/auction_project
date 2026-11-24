# Tasks: java21-boot35

## 1. Security behavior safety net (one commit)

- [ ] 1.1 Add an api-gateway test for the current access rules: anonymous, USER and ADMIN requests against `/api/v1/auth`, `/api/v1/users/**`, `/api/v1/bids/**` and `/swagger-ui/index.html`, asserting today's allow or deny outcome for each. Verify: the test passes on Boot 2.7.18.
- [ ] 1.2 Add an api-gateway test that issues a token through the login flow and gets it accepted by `JwtFilter`. Verify: the test passes.
- [ ] 1.3 Verify `./mvnw clean verify` is green on JDK 17, then commit.

## 2. Java 21 and current quality tools on Boot 2.7.18 (one commit)

- [ ] 2.1 Set `maven.compiler.release` to 21. Remove the hard-coded `<source>/<target>` 17 from the compiler plugin. Move Lombok to ≥ 1.18.34 and MapStruct to 1.6.3. Verify: `./mvnw -q compile` succeeds on JDK 21.
- [ ] 2.2 Move to SpotBugs 4.10.x, PMD 7.x through a current `maven-pmd-plugin`, and current Checkstyle through a current `maven-checkstyle-plugin`. Re-check the versions at apply time. Verify: each plugin runs on JDK 21 without a configuration error.
- [ ] 2.3 Migrate the rules PMD 7 renamed in `codequality/pmd-ruleset.xml`, keeping the same priorities. Verify: PMD loads the ruleset with no "rule not found" warnings.
- [ ] 2.4 Fix every new Checkstyle, PMD and SpotBugs violation in code, without editing thresholds or excluding rules. Verify: the quality plugins pass.
- [ ] 2.5 Set the CI `java-version` to 21. Verify `./mvnw clean verify` is green on JDK 21, then commit.

## 3. Boot 3.5.16, jakarta, Security 6, springdoc 2 (one commit)

- [ ] 3.1 Run OpenRewrite `UpgradeSpringBoot_3_5` once, keeping `javax.xml.bind` for jjwt 0.9.1. Switch the BOMs to Boot 3.5.16 and Spring Cloud 2025.0.x. Verify: `grep -rE 'import javax\.(servlet|validation|annotation)'` over `*/src` returns nothing.
- [ ] 3.2 Rewrite `SecurityConfig` with the lambda DSL and `requestMatchers`, keeping the same rules. Verify: the access-rule test from 1.1 passes unchanged.
- [ ] 3.3 Replace `springdoc-openapi-ui` with `springdoc-openapi-starter-webmvc-ui` 2.x. Verify: a context test confirms `/v3/api-docs` is served in each service.
- [ ] 3.4 Verify that the token test from 1.2 passes and `./mvnw clean verify` is green on JDK 21, then commit.
