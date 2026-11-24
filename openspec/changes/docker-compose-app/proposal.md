# Proposal: docker-compose-app

Order: 11 of 13. Depends on: `runtime-startup` (runnable jars, Flyway at startup), `outbox-publisher` (Kafka in compose) and `settlement-consumer`.

## Why

Running the system today takes three IDE run configurations, a manual migration step (removed in `runtime-startup`) and hand-set environment variables. A reviewer who clones the repo should start everything with one command. The README also claims the two services are reachable only through the gateway, but nothing enforces that.

## What Changes

- A Dockerfile per service. It builds the module from source in a JDK 21 stage, with the Maven cache mounted and tests and quality gates skipped (CI runs them). It then copies the extracted layers onto a Temurin 21 JRE image, which runs as a non-root user with `-Duser.timezone=UTC`.
- `docker-compose.yml` is restructured:
  - The default (no profile) runs the infrastructure for local IDE development: `users-db`, `auction-db` and `kafka`. Their host ports are as today, but bound to `127.0.0.1` only.
  - Profile `app` adds the three services, with actuator-based healthchecks and `depends_on: condition: service_healthy`.
  - Only the gateway publishes a host port (8080) in the app profile.
- The database services are renamed from `users-service`/`auction-service` to `users-db`/`auction-db`, so they don't collide with the application service names.
- `.env.example` lists `JWT_SECRET` (≥ 32 bytes) and `EXCHANGE_API_KEY` with placeholder values. `.env` is already ignored (`baseline-build`).
- `.dockerignore` keeps `target/`, `.git` and IDE files out of the build context.
- The README "Running locally" section leads with the one-command start.

Deviations from brief: none. Relative to the architect's T9, the images build from source instead of copying a prebuilt jar, so a clean clone works with `docker compose --profile app up --build` alone.

## Non-goals

- Kubernetes manifests, image publishing or multi-architecture builds.
- TLS or authentication for the internal services. Network isolation is the control, and it's documented.

## Capabilities

### New Capabilities
- `deployment`: how the whole system is started, and which parts are reachable from outside.

### Modified Capabilities
None.

## Impact

- New: `api-gateway/Dockerfile`, `auction-service/Dockerfile`, `users-service/Dockerfile`, `.dockerignore`, `.env.example`.
- Changed: `docker-compose.yml` (service renames and the app profile), and `application.yaml` defaults overridable through environment variables (`SPRING_DATASOURCE_URL`, `SERVICES_*_URL`, `SPRING_KAFKA_BOOTSTRAP_SERVERS`).
- README "Running locally".
