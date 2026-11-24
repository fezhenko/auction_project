# Tasks: e2e-smoke-secret-scan

## 1. E2E seed and compose override (one commit)

- [ ] 1.1 Add `e2e/db/R__e2e_seed.sql`: pgcrypto, plus an admin, a seller with one ON_SELL item at price 100, and a buyer with balance 1000. Emails are ≤ 30 characters. Passwords come from Flyway placeholders. The script is idempotent (`ON CONFLICT (email)` and `WHERE NOT EXISTS` for the item). Add `docker-compose.e2e.yml`, which mounts it into users-service and sets:
  - the Flyway location;
  - the placeholders through `SPRING_APPLICATION_JSON`;
  - `AUCTION_DURATION=PT20S`, the 2-second crons and a 1-second relay interval.
  Verify: with passwords from `openssl rand -hex 32`, `docker compose -f docker-compose.yml -f docker-compose.e2e.yml --profile app up --build -d --wait` succeeds, and the E2E admin can log in at `/api/v1/auth`. A successful login also proves that Flyway resolved the camelCase placeholder names.
- [ ] 1.2 Verify that restarting the E2E stack with new passwords re-runs the seed without errors or duplicate users and items, and that `docker compose --profile app up` without the override does not create the E2E users.
- [ ] 1.3 Verify `./mvnw clean verify` is green, then commit.

## 2. Smoke test module (one commit)

- [ ] 2.1 Add the `e2e-tests` module (failsafe runs only with `-Pe2e`; `java.net.http.HttpClient`, Awaitility, the PostgreSQL JDBC driver). Verify: `./mvnw clean verify` without `-Pe2e` runs no E2E test.
- [ ] 2.2 Implement `AuctionSettlementSmokeIT`. It runs: seller login → create auction → attach item → schedule start at now + 5 s → buyer login → join → wait for IN_PROGRESS → bid 100 → wait for FINISHED. Then it asserts:
  - the buyer's balance is 900 and the seller's has grown by 100 (admin `GET /api/v1/users`);
  - the item is SOLD and the settlement row is SETTLED (users database).
  Verify: it passes locally against the E2E stack with `./mvnw -pl e2e-tests -Pe2e verify`.
- [ ] 2.3 Document in the README how to run it locally, including generating the variables. Verify: the documented commands work as written.
- [ ] 2.4 Verify `./mvnw clean verify` is green, then commit.

## 3. CI jobs (one commit)

- [ ] 3.1 Add job `e2e` (`needs: build`), following design decision 4. Verify: on the pushed branch, `gh workflow run ci.yml --ref modernize/java21-boot-kafka` runs the `e2e` job green, and its log contains no secret values.
- [ ] 3.2 Add job `secret-scan` (`pull_request` only, `fetch-depth: 0`, pinned gitleaks image, `git --log-opts "<base>..<head>" --redact`). Verify: on the PR, the job runs green. A throwaway local test commit with a fake key pattern makes `gitleaks git --log-opts HEAD~1..HEAD` fail locally; that commit is never pushed.
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.
