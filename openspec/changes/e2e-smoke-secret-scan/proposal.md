# Proposal: e2e-smoke-secret-scan

Order: 11 of 12. Depends on: `auction-lifecycle-fixes`, `settlement-consumer` and `docker-compose-app`.

## Why

Each service is integration-tested on its own, but nothing proves the whole chain through the public API:
- gateway;
- auction lifecycle;
- outbox and relay;
- Kafka;
- settlement consumer;
- balances.
Decision D rules out running both services in one JVM, so the proof has to come from the real compose stack. The repo's history also contains secrets that have since been rotated, and nothing stops a new one from being committed.

## What Changes

- New Maven module `e2e-tests`, which runs only with `-Pe2e`. One smoke test drives the flow through `localhost:8080`:
  - the seller logs in, creates an auction, attaches an owned item, and schedules the start a few seconds ahead;
  - the buyer logs in, joins and bids;
  - the auction finishes, using a short duration;
  - the buyer's and seller's balances move by the final price, and the item is SOLD.
- The test users are seeded by a repeatable Flyway script that only the E2E compose override mounts. Their passwords come from environment variables, hashed with BCrypt by PostgreSQL `pgcrypto`, and CI generates them per run. Nothing depends on the dev seed passwords, and no password is committed.
- `docker-compose.e2e.yml` override:
  - a short `auction.duration` and second-level crons;
  - the E2E Flyway location;
  - placeholders for the E2E passwords.
- A separate CI job `e2e`:
  - starts the stack with `--wait`;
  - runs the smoke test;
  - prints service logs on failure;
  - always tears down.
- A CI job `secret-scan` on pull requests runs gitleaks over the PR's commits only (base..head), with findings redacted. History is not scanned, because it still holds rotated secrets.

Deviations from brief: none. These are the architect's T10 items. The brief's integration-test item is covered by `testcontainers-postgres`, `outbox-publisher` and `settlement-consumer`.

## Non-goals

- Load or performance tests.
- Scanning or rewriting git history.
- A test against a deployed environment.

## Capabilities

### New Capabilities
None. This adds test and CI infrastructure only (`skip_specs: true`). The E2E seed is test-only data that the production images never load.

### Modified Capabilities
None.

## Impact

- New: the `e2e-tests` module (added to the root `<modules>`, skipped unless `-Pe2e`), `docker-compose.e2e.yml`, `e2e/db/R__e2e_seed.sql`.
- `.github/workflows/ci.yml`: new `e2e` and `secret-scan` jobs.
- README: how to run the E2E test locally.
