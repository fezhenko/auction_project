# Design: e2e-smoke-secret-scan

## Context

- The seeded dev accounts (`test@admin.com`, `test@user.com`) store BCrypt hashes. Their plaintext passwords aren't in the repo, and CI must not need them.
- The gateway exposes no endpoint to read an item's state or to top up a balance. Admin `GET /api/v1/users` lists users with balances.
- In the `app` profile, the databases keep their dev host ports (`docker-compose-app`, decision 5).

## Goals / Non-Goals

**Goals:**
- One deterministic smoke test of the money path through the public API, run in CI on every PR.
- Block new secrets in PRs.

**Non-Goals:**
- Covering every endpoint end to end. The integration tests do that per service.

## Decisions

1. **The E2E seed is a repeatable migration at a filesystem location mounted only by `docker-compose.e2e.yml`.** It's added through `SPRING_FLYWAY_LOCATIONS=classpath:db/migration,filesystem:/e2e-migrations`.
   - It creates an admin, a seller with one ON_SELL item (price 100), and a buyer with balance 1000.
   - Passwords come from Flyway placeholders, hashed by `crypt('${e2eAdminPassword}', gen_salt('bf', 10))`, which produces `$2a$` hashes that Spring's `BCryptPasswordEncoder` verifies. `CREATE EXTENSION IF NOT EXISTS pgcrypto` is part of the script.
   - The placeholders are passed as `SPRING_APPLICATION_JSON={"spring":{"flyway":{"placeholders":{"e2eAdminPassword":…}}}}`, not as `SPRING_FLYWAY_PLACEHOLDERS_*` variables. Relaxed binding lowercases map keys from environment variables and turns `_` into `.`, which would mangle the placeholder names.
   - It's repeatable (`R__`), so no version number can ever collide with a real migration. The placeholder values feed its checksum, so it re-runs whenever the passwords change. It must therefore be idempotent: users get `INSERT … ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password`, and the item insert is guarded by `WHERE NOT EXISTS`.
   - E2E emails are at most 30 characters (for example `e2e-seller@test.local`), because `buyers.email` and `sellers.email` are `varchar(30)`.
   - Rejected: put the dev seed passwords in a CI secret. That ties CI to an undocumented value.
   - Rejected: add a test-only endpoint to the services. That's attack surface in production images.
2. **The assertions use the public API where one exists, and the users database where none does.**
   - Balances come from admin `GET /api/v1/users`.
   - The item state and the settlement row are read through JDBC on the dev-published `users-db` port.
   - That's documented in the test, so nobody mistakes it for a public API.
3. **Timing comes from configuration, not sleeps.** Through the override, `AUCTION_DURATION=PT20S`, the start and finish crons run every 2 s, and the relay interval is `PT1S`. The test polls with Awaitility (timeout 90 s) for each state.
4. **A separate CI job** (`needs: build`), so a broken environment doesn't hide unit-test results:
   - Secrets (`JWT_SECRET` and the E2E passwords) are generated per run with `openssl rand -hex 32`, masked with `::add-mask::`, and never echoed. Hex output keeps quotes, `$` and `{` out of the values, which matters because the passwords are substituted into a SQL string literal by Flyway.
   - On failure it runs `docker compose logs`.
   - It always runs `down -v`.
5. **gitleaks** comes from the official container image, pinned by version, run over the PR's commit range: `gitleaks git --log-opts "<base>..<head>" --redact`, with `fetch-depth: 0`.
   - Rejected: the gitleaks GitHub Action, which needs a licence key for organisations and adds a third-party action.
   - Rejected: scanning full history, which would fail on the known, rotated secrets.

No migration of the services' own schemas is added by this change.

## Risks / Trade-offs

- [The E2E test is flaky on slow CI runners] → Health-gated start (`--wait`) and generous polling timeouts. The job is required, but it's separate from `build`.
- [The DB-level assertions couple the test to the schema] → Limited to two reads (item state and settlement outcome), and documented.
- [A false positive from gitleaks blocks a PR] → Allowlist the specific placeholder in `.gitleaks.toml` with a comment, never a whole path or rule.
