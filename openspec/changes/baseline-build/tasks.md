# Tasks: baseline-build

## 1. Green build on the Boot 2.7.18 BOMs (one commit)

- [ ] 1.1 Import `spring-boot-dependencies` 2.7.18 and `spring-cloud-dependencies` 2021.0.9 (`<scope>import</scope>`) in the root pom. Remove every version they manage from the root and module poms, including `spring-security-test` 5.6.0 in api-gateway. Verify: `mvn -q dependency:tree -pl api-gateway -Dincludes=org.springframework.security` lists one Security version.
- [ ] 1.2 Add `@Disabled("replaced by a Testcontainers test in change testcontainers-postgres")` to `AuctionItemsTest`. Verify: the surefire report shows it as skipped.
- [ ] 1.3 Fix in code each SpotBugs finding reported now that all modules reach `package`, without touching `codequality/`. Verify: `mvn clean verify` on JDK 17 shows SpotBugs passing for all three modules.
- [ ] 1.4 Verify `mvn clean verify` is green on JDK 17, then commit.

## 2. Reproducible build from any checkout (one commit)

- [ ] 2.1 Add the script-only Maven Wrapper (`mvn wrapper:wrapper -Dtype=only-script`). Verify: `./mvnw -v` prints the pinned Maven version.
- [ ] 2.2 Replace `../../auction_project/codequality/…` with `${maven.multiModuleProjectDirectory}/codequality/…` in the build and reporting sections, and delete the unused `checkstyle.version` property. Verify: clone the branch into a folder with a different name, and `./mvnw clean verify` passes there.
- [ ] 2.3 Add `target/`, `.env`, `*.iml` and `.DS_Store` to `.gitignore`. Verify: `git status` is clean after a full build.
- [ ] 2.4 Verify `./mvnw clean verify` is green on JDK 17, then commit.

## 3. CI (one commit)

- [ ] 3.1 Rewrite `.github/workflows/ci.yml`: current major versions of `actions/checkout` and `actions/setup-java`, `distribution: temurin`, `java-version: 17`, `cache: maven`, and run `./mvnw -B clean verify`. Keep the `push` (main), `pull_request` (main) and `workflow_dispatch` triggers.
- [ ] 3.2 Verify `./mvnw clean verify` is green locally, then commit. After the branch is pushed, verify that `gh workflow run ci.yml --ref modernize/java21-boot-kafka` finishes green (the first green CI run).

## 4. README matches the code (one commit)

- [ ] 4.1 Correct the README overview and "Known limitations":
  - Creating an auction through the gateway fails.
  - The gateway can't set a start date.
  - Finishing rolls back because the item-SOLD call fails.
  - Settlement does nothing.
  - Drop the line about failing tests.
  - Point to the changes that fix each item.
  Verify: every README sentence about behavior matches the code paths in `AuctionService` (gateway and auction-service) and `UsersService`.
- [ ] 4.2 Verify `./mvnw clean verify` is green, then commit.
