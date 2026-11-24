# Design: observability-otel-lgtm

## Context

- After `boot42-upgrade`, all services are on Boot 4.2.0, which manages Micrometer Tracing 1.8.0 and OpenTelemetry 1.68.0, and ships `spring-boot-starter-opentelemetry`. The starter pulls in `micrometer-registry-otlp`, `micrometer-tracing-bridge-otel` and `opentelemetry-exporter-otlp`.
- Relevant property names, confirmed in the 4.2.0 configuration metadata:
  - tracing: `management.opentelemetry.tracing.export.otlp.endpoint`, `management.tracing.export.otlp.enabled`
  - metrics: `management.otlp.metrics.export.url`, `management.otlp.metrics.export.enabled` (default **true**)
  - logs: `management.opentelemetry.logging.export.otlp.endpoint`, `management.logging.export.otlp.enabled`
- The actuator exposes only health and info (`boot42-upgrade`).
- `outbox-publisher` stores events in `event_outbox` and publishes them from a dedicated relay thread. `settlement-consumer` consumes them in users-service.
- `grafana/otel-lgtm` 0.41.1 (2026-11-20) bundles an OTel collector (OTLP on 4317 gRPC and 4318 HTTP), Tempo, Loki, Prometheus and Grafana (port 3000).

## Goals / Non-Goals

**Goals:**
- The behavior in `specs/observability`.
- Zero impact on tests and IDE runs unless export is switched on.

**Non-Goals:**
- Production telemetry concerns (see the proposal).

## Decisions

1. **Spring's own starter, not the OpenTelemetry Java agent or the community starter.** It integrates with Micrometer Observation, which Spring MVC, Spring Kafka, Spring Cloud OpenFeign and `@Scheduled` already emit. There's no `-javaagent` in the Dockerfiles.
   - Rejected: the Java agent. It's broader auto-instrumentation, but it adds a second instrumentation model next to Micrometer and complicates the images.
   - Rejected: the community `opentelemetry-spring-boot-starter`. It conflicts with Spring's starter beans.
2. **Export is off by default and switched on by environment.** `application.yaml` sets `management.tracing.export.otlp.enabled`, `management.otlp.metrics.export.enabled` and `management.logging.export.otlp.enabled` to `false`. The override file sets them to `true`, with the endpoints `http://lgtm:4318/v1/{traces,metrics,logs}` and `management.tracing.sampling.probability=1.0`. Export is asynchronous (batch span processor and periodic metric reader), so an unreachable collector never blocks a request.
3. **Trace context through the outbox.**
   - Migration `V23__event_outbox_add_trace_parent.sql` adds `trace_parent text NULL` to `event_outbox`.
   - In the finish transaction, the current span's context is serialized through the configured `Propagator` (W3C) into `trace_parent`.
   - The relay opens an `Observation` whose context is extracted from `trace_parent` (a receiver-style carrier, so the stored context becomes the parent), and keeps it in scope around `send(...).get()`. The template's observation is then created under it, and injects `traceparent` into the record headers. A span opened through the raw `Tracer` API would lose to the `@Scheduled` relay run's own observation as the current parent, so the send would attach to the relay run instead of to the finish.
   - The listener observation in users-service extracts it, so the settlement runs in the same trace.
   - Rejected: a span link instead of parent-child. It's more accurate for long delays, but Tempo shows links less clearly, and the local goal is "one trace from finish to settlement".
   - The column is nullable. Events recorded before this change, or with tracing off, publish without the header.
4. **Kafka and Feign observation.**
   - `spring.kafka.template.observation-enabled=true` (auction-service) and `spring.kafka.listener.observation-enabled=true` (users-service).
   - Feign: add `io.github.openfeign:feign-micrometer` so Spring Cloud OpenFeign registers its Micrometer observation capability. A test confirms the header is sent instead of assuming it.
   - **Trace context never leaves the system.** users-service's `ExchangeClient` calls the external apilayer API. That client gets a per-client configuration without the observation capability, plus a request interceptor that removes `traceparent`/`tracestate`, so internal trace ids aren't sent to a third party.
   - Tests set `management.tracing.sampling.probability=1.0` in their test configuration. Boot's default is 0.1, which would make in-memory span assertions flaky.
   - `opentelemetry-logback-appender-1.0` isn't in the Boot 4.2.0 BOM. Its version is pinned in the root `dependencyManagement` to the OpenTelemetry instrumentation release built on SDK 1.68.0.
5. **Logs.** `opentelemetry-logback-appender-1.0` is registered in `logback-spring.xml` and installed against the Boot-managed `OpenTelemetry` bean at startup. It's a no-op while log export is disabled. The console pattern keeps Boot's correlation field (`logging.pattern.correlation`), so `traceId`/`spanId` appear locally even without Loki.
6. **Local stack as an override file**, `docker-compose.observability.yml`, used with `docker compose -f docker-compose.yml -f docker-compose.observability.yml --profile app up --build`.
   - It adds `lgtm` (`grafana/otel-lgtm:0.41.1`, pinned).
   - Ports: `127.0.0.1:3000` (Grafana), and `127.0.0.1:4317` and `127.0.0.1:4318` (for IDE runs).
   - It adds the export environment to the three services, and makes them depend on `lgtm` being healthy.
   - Rejected: a compose profile. Profiles can't change other services' environment, so export would need separate switches.

Flyway migration (auction-service): `V23__event_outbox_add_trace_parent.sql` adds the nullable column `event_outbox.trace_parent`.

## Risks / Trade-offs

- [Property names move again in a Boot patch, and a renamed key is silently ignored] → The names come from the 4.2.0 metadata. Task 1.1's context tests assert the exporter beans exist when the switches are on and are absent when off, so an unbound key fails the build.
- [`otel-lgtm` needs about 1–1.5 GB of RAM] → It's opt-in through the override file, not part of the default `app` start.
- [Grafana in `otel-lgtm` runs with default credentials] → Bound to `127.0.0.1` only. That's documented in the README.
- [Parent-child across a long outbox delay produces a long trace] → Acceptable locally. The relay's span starts at send time, so the gap is visible as the actual wait.
