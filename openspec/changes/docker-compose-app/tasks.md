# Tasks: docker-compose-app

## 1. Service images (one commit)

- [ ] 1.1 Add `.dockerignore` and a multi-stage `Dockerfile` for each service, following design decision 1. Verify: `docker build -f <module>/Dockerfile .` succeeds for all three, and `docker run --rm --entrypoint id <image> -u` prints a non-zero id. Note from baseline-build: the build stage must install `unzip` before `./mvnw`. Without it, the script-only wrapper downloads the `.tar.gz`, which fails the SHA-256 pinned for the zip (`eclipse-temurin` images ship without `unzip`).
- [ ] 1.2 Verify that `docker history <image>` and `docker run --rm --entrypoint env <image>` show no secret values (no `JWT_SECRET` or `EXCHANGE_API_KEY` values baked in).
- [ ] 1.3 Verify `./mvnw clean verify` is green, then commit.

## 2. Compose app profile (one commit)

- [ ] 2.1 Rename the database services to `users-db`/`auction-db` (`postgres:17-alpine`, `pg_isready` healthchecks). Keep Kafka with its healthcheck. Bind every infrastructure port to `127.0.0.1` (`127.0.0.1:15432:5432`, `127.0.0.1:15433:5432`, `127.0.0.1:9092:9092`). Add the three services under `profiles: [app]`, with environment overrides, healthchecks and `depends_on: condition: service_healthy`. Only `api-gateway` publishes `8080:8080`. Verify: `docker compose config` shows no host ports for auction-service or users-service, and `host_ip: 127.0.0.1` on every infrastructure port.
- [ ] 2.2 Add `.env.example` with placeholder values (a ≥ 32-byte `JWT_SECRET` placeholder that is clearly fake, and an `EXCHANGE_API_KEY` placeholder). Verify: `git grep` finds no real secret values (spot-check against gitleaks in `e2e-smoke-secret-scan`).
- [ ] 2.3 Verify these on a fresh `git clone` into a temp folder:
  - `cp .env.example .env && docker compose --profile app up --build -d --wait` reports every service healthy;
  - an ADMIN login against `localhost:8080` returns a token;
  - `nc -z localhost 8081` fails;
  - starting without `JWT_SECRET` leaves the gateway unhealthy, with a log line naming the variable.
- [ ] 2.4 Verify `./mvnw clean verify` is green, then commit.

## 3. README (one commit)

- [ ] 3.1 Rewrite "Running locally": the one-command start first (requirements: Docker; copy `.env.example`; `docker compose --profile app up --build`), then the IDE development flow (`docker compose up -d` for the infrastructure). Verify: following the README on a clean clone works as written.
- [ ] 3.2 Verify `./mvnw clean verify` is green, then commit.
