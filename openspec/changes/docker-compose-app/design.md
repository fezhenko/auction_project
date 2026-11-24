# Design: docker-compose-app

## Context

- After `runtime-startup`, each service builds a layered jar and applies migrations at startup.
- `outbox-publisher` added Kafka to compose.
- Today's compose names the database services `users-service` and `auction-service`, with ports 15432 and 15433.
- The service URLs and datasource URLs in `application.yaml` default to `localhost`.

## Goals / Non-Goals

**Goals:**
- The behavior in `specs/deployment`, with nothing needed beyond Docker and a `.env`.

**Non-Goals:**
- A fast image rebuild for every code change. Developers keep using the IDE against the default profile.

## Decisions

1. **Multi-stage build from source.**
   - The context is the repo root, with `-f <module>/Dockerfile`.
   - Stage 1 (`eclipse-temurin:21-jdk`): `./mvnw -B -pl <module> -am package -DskipTests -Dcheckstyle.skip -Dpmd.skip -Dspotbugs.skip`, with `--mount=type=cache,target=/root/.m2`.
   - Stage 2 extracts layers with `java -Djarmode=tools -jar app.jar extract --layers --launcher`.
   - Stage 3 (`eclipse-temurin:21-jre`) copies the layers in dependency order, then runs `USER 10001` with the `JarLauncher` entrypoint.
   - Rejected: copy a prebuilt jar. A clean clone would need a separate `mvn package` first, which breaks the one-command claim.
   - Skipping tests and gates in the image is fine, because CI runs `verify` on every commit.
2. **Profiles instead of two compose files.** One file. The default profile is the dev infrastructure, and `app` adds the services. `docker compose up` keeps today's developer workflow.
3. **Healthchecks:**
   - Services: `wget -qO- http://localhost:<port>/actuator/health | grep -q UP`. The JRE base image includes `wget`; if it doesn't at apply time, use `curl` from the image, or add a minimal package.
   - PostgreSQL: `pg_isready`.
   - Kafka: `kafka-broker-api-versions.sh`.
4. **Configuration through environment variables** using Spring's relaxed binding (`SPRING_DATASOURCE_URL`, `SERVICES_USERS_SERVICE_URL`, `SPRING_KAFKA_BOOTSTRAP_SERVERS`). The YAML defaults stay correct for IDE runs.
5. **No host ports for the internal services in `app`. Infrastructure ports are bound to the loopback interface.** The databases (`127.0.0.1:15432`, `127.0.0.1:15433`) and Kafka (`127.0.0.1:9092`) keep host ports so IDE runs and the E2E database assertions work. Binding them to `127.0.0.1` keeps them off the network: on `0.0.0.0`, anyone on the LAN could publish a forged `auction.finished` event and move money, or log in to PostgreSQL with the dev credentials. That they're still reachable from the host itself is listed as a known limitation.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [The first image build is slow (Maven downloads)] → The BuildKit cache mount makes rebuilds fast. The README says the first start takes a few minutes.
- [Three images build overlapping reactor modules] → Accepted for simplicity. A shared builder stage is a follow-up.
- [Apple Silicon versus amd64 base images] → The Temurin and apache/kafka images are multi-architecture.
