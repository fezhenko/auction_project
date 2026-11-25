# Design: java21-boot35

## Context

After `baseline-build`, the build is green on Boot 2.7.18 and JDK 17 with Checkstyle 8.29, PMD 6 (plugin 3.19.0), SpotBugs 4.7.1, Lombok 1.18.24 and MapStruct 1.4.2. Those tool versions can't run on Java 21 bytecode, so the JDK and the tools have to move together.

## Goals / Non-Goals

**Goals:**
- Green `mvn clean verify` on JDK 21 with Boot 3.5.16.
- No `javax.servlet`, `javax.validation` or `javax.annotation` imports left.

**Non-Goals:**
- Changes in behavior. HTTP routes, the security matrix and JSON output stay identical.

## Decisions

1. **Two commits instead of one.**
   - Commit A: Java 21, plus the Lombok, MapStruct and quality-tool bumps, still on Boot 2.7.18. Boot 2.7.18 runs on Java 21.
   - Commit B: Boot 3.5.16, Spring Cloud 2025.0.x, jakarta, Security 6 and springdoc 2.
   The JDK and its analyzers are coupled, so they share a commit. The framework is a separate concern.
   - Rejected: a single commit. A failure would mix tool, JDK and framework causes.
2. **OpenRewrite, run once and not kept in the pom.** Use `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_5` from `rewrite-spring`, run through `mvn -U org.openrewrite.maven:rewrite-maven-plugin:run`. It rewrites imports, poms and property keys consistently. The rewrite plugin isn't added to the build.
   - Rejected: sed over imports. It misses property renames and pom coordinates.
3. **Spring Cloud 2025.0.x for this commit only.** That train is end-of-life. That's acceptable for a commit `boot42-upgrade` replaces right away.
4. **PMD 7 ruleset migration keeps the rules' intent.** Map renamed rules to their PMD 7 names. Rules PMD 7 removed get their documented replacement, or are dropped with a one-line comment in the ruleset naming the removal. Thresholds and priorities stay unchanged.

## Risks / Trade-offs

- [PMD 7 and current Checkstyle report many new violations] → Fix them in code within commit A, in one commit per module if the list is long, each green.
- [OpenRewrite also moves `javax.xml.bind` to `jakarta.xml.bind`, which breaks jjwt 0.9.1 (it needs `javax.xml.bind.DatatypeConverter`)] → Exclude `javax.xml.bind` from the rewrite, or keep jaxb-api on the gateway classpath until `boot42-upgrade` replaces jjwt. Verify with a gateway test that issues and parses a token.
- [Security 6 changes the default matcher semantics (MVC vs. ant matchers, trailing slashes)] → Before the migration, add a gateway test that checks the role matrix: anonymous, USER and ADMIN against `/api/v1/users/**`, `/api/v1/bids/**`, `/swagger-ui/**` and `/api/v1/auth`. Keep it green after.

## Migration Plan

Build-only, with no data change. Rollback means reverting the two commits.
