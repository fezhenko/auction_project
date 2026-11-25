# Design: baseline-build

## Context

The root pom pins about 20 versions by hand, including Boot starters 2.7.5, `spring-context` 5.3.23 and `spring-security-test` 5.6.0. Nothing imports a BOM, so transitive versions drift apart, and the Security test jar clash comes from that drift. The build fails in auction-service tests, so api-gateway never reaches `package`, and SpotBugs has never analyzed it. Where SpotBugs does run it reports 0 bugs, because `codequality/spotbug.xml` puts five `<Bug category>` children in one `<Match>` and SpotBugs ANDs them: the include filter matches nothing. Written as one comma-separated category list, it reports 4 findings (3 in users-service, 1 in api-gateway). There is no formatter; Checkstyle reports 160 warnings (151 `ImportOrder`, 9 `LineLength`). The PMD and SpotBugs rule paths are written as `../../auction_project/codequality/…`. Checkstyle is pinned to 8.29 in the plugin dependency, and the `checkstyle.version` property (10.3.3) is unused.

## Goals / Non-Goals

**Goals:**
- `mvn clean verify` is green on JDK 17 from a fresh clone in any folder.
- CI runs that build on every push to `main` and on every pull request.

**Non-Goals:**
- Upgrading the quality tools. Checkstyle, PMD and SpotBugs move in `java21-boot35`, together with the JDK they have to analyze. Adding Spotless and correcting the SpotBugs filter are not upgrades: the plugin versions stay.

## Decisions

1. **Import the BOMs, not `spring-boot-starter-parent`.** The root pom stays its own parent, and only versions move. A parent also changes plugin defaults (resource filtering delimiters, compiler flags), which would mix two kinds of change into the baseline.
   - Rejected: keep the hand-pinned versions and bump `spring-security-test` only. That fixes one clash and leaves the drift that caused it.
2. **Script-only Maven Wrapper** (`mvn wrapper:wrapper -Dtype=only-script`). It needs no `maven-wrapper.jar`, and the existing `*.jar` ignore rule stays valid. The distribution is pinned with `distributionSha256Sum`. The wrapper plugin does not compute it, so a Maven bump means taking the SHA-256 of a zip whose SHA-512 matches the published `.sha512`. Rule files resolve from `${maven.multiModuleProjectDirectory}`, which is the nearest directory with `.mvn/`, so `.mvn/` must stay committed. Under Maven 4 the replacement is `${session.rootDirectory}`.
3. **Disable `AuctionItemsTest` instead of fixing it now.** A real fix needs a database, which arrives with Testcontainers in `testcontainers-postgres`. The disable reason names that change, so the gap stays visible.
4. **Fix SpotBugs findings in code.** Excluding patterns or lowering the threshold breaks the rule that quality gates are never loosened.
5. **Leave Checkstyle at 8.29 here, and delete the unused `checkstyle.version` property** so the pom stops claiming 10.3.3. The upgrade happens in `java21-boot35`.

6. **Gateway tests stub the downstream services with WireMock** (`spring-cloud-contract-wiremock`, managed by the Spring Cloud BOM). `AbstractConfiguration` starts one WireMock server on a random port, points `services.auction-service.url` and `services.users-service.url` at it, and resets stubs after each test method. Stub bodies are JSON files shaped like the real services' responses. The project owner decided this during apply.
   - Rejected: `@MockBean` on the Feign clients. It skips Feign and Jackson decoding, which is the boundary the test should cover, and Boot 4 removes it.
   - Rejected: the standalone `org.wiremock` artifact. No BOM manages it, so it would add a hand-pinned version.
   - Trade-off: one server for both services means a client pointed at the wrong service URL still passes. No stub paths collide today.
   - Known gap: nothing ties the stub JSON to auction-service's real output. A shared DTO in `commons-module` closes it.

7. **Correct the SpotBugs include filter in `codequality/`** (one comma-separated `category` list) and fix the 4 findings in code. This tightens the gate, so it does not break the rule against loosening quality gates. The owner approved the edit to `codequality/`. Without it, `java21-boot35`'s SpotBugs upgrade would also check nothing.
8. **Spotless with palantir-java-format** (`spotless-maven-plugin` 3.12.0, palantir 2.103.0). Palantir's 4-space indent and 120-column limit match the Checkstyle rules. Its `importOrder` step is set to Checkstyle's `ImportOrder` groups. A trial on all sources changed 143 files, kept the build green, and took Checkstyle from 160 warnings to 0.
   - `spotless:check` runs at `validate`, so `./mvnw verify` locally and in CI fail on unformatted code. CI also runs it as a separate, named step for a clear failure.
   - The one-time reformat is its own format-only commit, listed in `.git-blame-ignore-revs`.
   - A versioned `.githooks/pre-commit` hook (opt-in through `core.hooksPath`) is a convenience, not the gate. It formats the staged Java files in the working tree and blocks the commit if it changed them. It never stages files, so partly staged changes stay intact, and a partly staged unformatted change can slip past it. `spotless:check` in the build and in CI is the enforcement.
   - Rejected: google-java-format. Its 2-space indent breaks the error-level Checkstyle `Indentation` rule, and fixing that would loosen a rule.
   - Rejected: AOSP style (100 columns) and an Eclipse formatter file. AOSP rewraps more than Checkstyle's 120 columns need, and an Eclipse formatter file is one more file to maintain.
   - Rejected: `ratchetFrom`. It leaves old files unformatted, and CI would need full history.

## Risks / Trade-offs

- [The BOM changes a transitive version, and a passing unit test starts failing] → Read `mvn dependency:tree` before and after, and fix each regression before committing.
- [The corrected SpotBugs filter reports more than the 4 findings seen in the trial] → Fix them all in code in section 4. If the list is long, split it into one commit per module, each green.
- [The format-only commit hides authorship in `git blame`] → `.git-blame-ignore-revs` lists it; GitHub reads that file automatically.
- [A squash or rebase merge of the branch drops the format-only commit hash in `.git-blame-ignore-revs`, and loses the one-task-per-commit history] → Merge the PR with a merge commit. If it is squashed anyway, replace the hash with the one that lands on `main`.
- [A CI run on the branch needs a trigger before the PR exists] → Use `workflow_dispatch`, which the workflow already declares, with `gh workflow run ci.yml --ref <branch>`.

## Migration Plan

Build-only. Rollback means reverting the commits; no data or runtime state changes.
