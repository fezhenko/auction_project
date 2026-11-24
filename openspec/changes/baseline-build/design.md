# Design: baseline-build

## Context

The root pom pins about 20 versions by hand, including Boot starters 2.7.5, `spring-context` 5.3.23 and `spring-security-test` 5.6.0. Nothing imports a BOM, so transitive versions drift apart, and the Security test jar clash comes from that drift. The build fails in auction-service tests, so api-gateway never reaches `package`, and SpotBugs has never analyzed it. The PMD and SpotBugs rule paths are written as `../../auction_project/codequality/…`. Checkstyle is pinned to 8.29 in the plugin dependency, and the `checkstyle.version` property (10.3.3) is unused.

## Goals / Non-Goals

**Goals:**
- `mvn clean verify` is green on JDK 17 from a fresh clone in any folder.
- CI runs that build on every push to `main` and on every pull request.

**Non-Goals:**
- Upgrading the quality tools. Checkstyle, PMD and SpotBugs move in `java21-boot35`, together with the JDK they have to analyze.

## Decisions

1. **Import the BOMs, not `spring-boot-starter-parent`.** The root pom stays its own parent, and only versions move. A parent also changes plugin defaults (resource filtering delimiters, compiler flags), which would mix two kinds of change into the baseline.
   - Rejected: keep the hand-pinned versions and bump `spring-security-test` only. That fixes one clash and leaves the drift that caused it.
2. **Script-only Maven Wrapper** (`mvn wrapper:wrapper -Dtype=only-script`). It needs no `maven-wrapper.jar`, and the existing `*.jar` ignore rule stays valid.
3. **Disable `AuctionItemsTest` instead of fixing it now.** A real fix needs a database, which arrives with Testcontainers in `testcontainers-postgres`. The disable reason names that change, so the gap stays visible.
4. **Fix SpotBugs findings in code.** Excluding patterns or lowering the threshold breaks the rule that quality gates are never loosened.
5. **Leave Checkstyle at 8.29 here, and delete the unused `checkstyle.version` property** so the pom stops claiming 10.3.3. The upgrade happens in `java21-boot35`.

## Risks / Trade-offs

- [The BOM changes a transitive version, and a passing unit test starts failing] → Read `mvn dependency:tree` before and after, and fix each regression before committing.
- [SpotBugs reports many findings in api-gateway the first time it runs there] → Fix them in this change. If the list is long, split it into one commit per module, each green.
- [A CI run on the branch needs a trigger before the PR exists] → Use `workflow_dispatch`, which the workflow already declares, with `gh workflow run ci.yml --ref <branch>`.

## Migration Plan

Build-only. Rollback means reverting the commits; no data or runtime state changes.
