# Tasks: baseline-build

## 1. Green build on the Boot 2.7.18 BOMs (one commit)

- [x] 1.1 Import `spring-boot-dependencies` 2.7.18 and `spring-cloud-dependencies` 2021.0.9 (`<scope>import</scope>`) in the root pom. Remove every version they manage from the root and module poms, including `spring-security-test` 5.6.0 in api-gateway. Verify: `mvn -q dependency:tree -pl api-gateway -Dincludes=org.springframework.security` lists one Security version.
- [x] 1.2 Add `@Disabled("replaced by a Testcontainers test in change testcontainers-postgres")` to `AuctionItemsTest`. Verify: the surefire report shows it as skipped.
- [x] 1.2b Rewrite api-gateway `AuctionServiceTest` against WireMock (`spring-cloud-contract-wiremock`, managed by the Spring Cloud BOM, test scope). `AbstractConfiguration` starts one WireMock server on a random port and points `services.auction-service.url` and `services.users-service.url` at it, so no gateway test calls a live service. The stub body is a JSON file shaped like auction-service's `AuctionController` output (PLANNED, IN_PROGRESS and FINISHED auctions), and the test asserts the mapped fields. Verify: the test passes with auction-service and users-service stopped.
- [x] 1.3 Fix in code each SpotBugs finding reported now that all modules reach `package`, without touching `codequality/`. Verify: `mvn clean verify` on JDK 17 shows SpotBugs passing for all three modules. Result: every module logs `BugInstance size is 0`, but that is not evidence of clean code. `codequality/spotbug.xml` puts five `<Bug category>` children in one `<Match>`, SpotBugs ANDs them, and the include filter matches nothing. Section 4 fixes the filter and the findings it then reports.
- [x] 1.4 Verify `mvn clean verify` is green on JDK 17, then commit.

## 2. Reproducible build from any checkout (one commit)

- [x] 2.1 Add the script-only Maven Wrapper (`mvn wrapper:wrapper -Dtype=only-script`). Verify: `./mvnw -v` prints the pinned Maven version. Done: Maven 3.9.16 through maven-wrapper-plugin 3.3.4, with `distributionSha256Sum` taken from a zip whose SHA-512 matched Apache's published one. `.gitattributes` pins `mvnw` to LF and `*.cmd` to CRLF.
- [x] 2.2 Replace `../../auction_project/codequality/…` with `${maven.multiModuleProjectDirectory}/codequality/…` in the build and reporting sections, and delete the unused `checkstyle.version` property. Verify: clone the branch into a folder with a different name, and `./mvnw clean verify` passes there. Done: the `checkstyle.config.location` property moved to the same base, so a build started from a module directory also finds the rules.
- [x] 2.3 Add `target/`, `.env`, `*.iml` and `.DS_Store` to `.gitignore`. Verify: `git status` is clean after a full build.
- [x] 2.4 Verify `./mvnw clean verify` is green on JDK 17, then commit.

## 3. Spotless formatting (two commits)

- [ ] 3.1 Add `spotless-maven-plugin` 3.12.0 to the root build with a `java` block: `palantirJavaFormat` 2.103.0, `importOrder` `java,javax,,\#` (the Checkstyle `ImportOrder` groups, static imports last), `removeUnusedImports`, `trimTrailingWhitespace`, `endWithNewline`. Do not bind any goal yet. Run `./mvnw spotless:apply` and commit the result as a format-only commit (plugin config plus reformatted sources, no other edits). Verify: `./mvnw clean verify` is green on JDK 17 and Checkstyle reports no more warnings than before.
- [ ] 3.2 Bind `spotless:check` to the `validate` phase, next to Checkstyle and PMD. Add `.git-blame-ignore-revs` with the 3.1 commit hash. Add `.githooks/pre-commit`: it runs `./mvnw -q spotless:apply -DspotlessFiles=…` on the staged `*.java` files, and when that changes any of them it fails the commit with a message to review and `git add` them (it never stages files itself). Document the one-time `git config core.hooksPath .githooks` in the README's build section. Verify: an unformatted Java file makes `./mvnw verify` fail at `validate`, and the hook blocks a commit of that file and leaves it formatted but unstaged; then `./mvnw clean verify` is green, then commit.

## 4. Working SpotBugs gate (one commit)

- [ ] 4.1 Rewrite `codequality/spotbug.xml` so its five categories are one comma-separated `<Bug category="SECURITY,BAD_PRACTICE,STYLE,NOISE,EXPERIMENTAL"/>` (OR instead of AND). This tightens the gate; the owner approved editing `codequality/` for it. Verify: `./mvnw clean verify` now reports the findings instead of `BugInstance size is 0`.
- [ ] 4.2 Fix in code each finding the corrected filter reports, without suppressions or exclusions. Expected today: `JdbcConfig` NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE and `UsersController` RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE and `UsersService` SF_SWITCH_NO_DEFAULT (users-service), `AuctionService.createAuction` REC_CATCH_EXCEPTION (api-gateway). Keep behavior the same: the gateway still answers with `CreateAuctionException` when a downstream call fails. Verify: `./mvnw spotless:apply` changes nothing, `./mvnw clean verify` is green with `BugInstance size is 0` from a filter that matches, then commit.

## 5. CI (one commit)

- [ ] 5.1 Rewrite `.github/workflows/ci.yml`: current major versions of `actions/checkout` and `actions/setup-java`, `distribution: temurin`, `java-version: 17`, `cache: maven`, a separate "Check formatting" step that runs `./mvnw -B spotless:check` before the build, and run `./mvnw -B clean verify`. Keep the `push` (main), `pull_request` (main) and `workflow_dispatch` triggers.
- [ ] 5.2 Verify `./mvnw clean verify` is green locally, then commit. After the branch is pushed, verify that `gh workflow run ci.yml --ref modernize/java21-boot-kafka` finishes green (the first green CI run).

## 6. README matches the code (one commit)

- [ ] 6.1 Correct the README overview and "Known limitations":
  - Creating an auction through the gateway fails.
  - The gateway can't set a start date.
  - Finishing rolls back because the item-SOLD call fails.
  - Settlement does nothing.
  - Drop the line about failing tests.
  - Point to the changes that fix each item.
  Verify: every README sentence about behavior matches the code paths in `AuctionService` (gateway and auction-service) and `UsersService`.
- [ ] 6.2 Verify `./mvnw clean verify` is green, then commit.
