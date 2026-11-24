# Proposal: observability-otel-lgtm

Order: 14 of 14 (lowest priority). Depends on: `outbox-publisher`, `settlement-consumer`, `docker-compose-app` and `readme-architecture`.

## Why

When a bid fails or a settlement is rejected, the only evidence is three separate log streams with nothing tying them together. Nothing shows a request's path through gateway → auction-service → users-service, or how an auction's finish became a settlement seconds later via the outbox and Kafka. Locally, a developer should be able to open Grafana and follow one trace end to end.

## What Changes

- `spring-boot-starter-opentelemetry` (Boot 4.2) in api-gateway, auction-service and users-service, each with a `spring.application.name`. Traces and metrics go out over OTLP/HTTP, and logs too, through the OpenTelemetry Logback appender.
- Export is **off by default**: in IDE runs, tests and the plain `app` profile, the services start without a collector and log no export errors. It's switched on only by the observability compose override.
- Trace context propagates:
  - over HTTP, from the client to the gateway and on through Feign to the services;
  - over Kafka, through Spring Kafka observation on the template and the listener;
  - **through the outbox:** the finish transaction stores the current W3C `traceparent` in the outbox row, and the relay sends it as a record header. The settlement in users-service then continues the trace of the finish job that produced it.
- HTTP server telemetry and JVM metrics use OpenTelemetry semantic-convention names (for example `http.server.request.duration`). This is done through a small `OpenTelemetryConfiguration` per service, as in Spring's "OpenTelemetry with Spring Boot" post, so Grafana and standard OTel dashboards recognize them.
- Every log line carries `traceId` and `spanId`.
- `docker-compose.observability.yml` adds `grafana/otel-lgtm` (Grafana, Tempo, Loki, Prometheus and an OTel collector in one image). Grafana is on `127.0.0.1:3000`, and OTLP on `127.0.0.1:4317/4318` for IDE runs. The override also enables export with 100% sampling.
- The README gets an "Observability" section: how to start the stack and find a trace in Grafana.

Deviations from brief: beyond the brief. The owner added it after reviewing the proposals, at the lowest priority.

## Non-goals

- Production-grade telemetry: tail sampling, retention, alerting rules or dashboards beyond what `otel-lgtm` ships with.
- JDBC-level spans, for example through `datasource-micrometer`. That's a follow-up once its Boot 4.2 support is confirmed.
- Exposing `/actuator/prometheus` or other actuator endpoints. Telemetry is pushed over OTLP, so the `service-operations` rule (health and info only) still holds.
- Running `otel-lgtm` in CI.

## Capabilities

### New Capabilities
- `observability`: trace propagation across HTTP, Kafka and the outbox; log correlation; opt-in export; and the local Grafana stack.

### Modified Capabilities
None.

## Impact

- All three service poms: `spring-boot-starter-opentelemetry` and `opentelemetry-logback-appender-1.0`.
- `application.yaml` in each service: application name, export switches (off), and the log correlation pattern.
- auction-service: migration `V23__event_outbox_add_trace_parent.sql`; the finish transaction and the relay read and write `traceparent`.
- users-service: listener observation enabled.
- New file `docker-compose.observability.yml`. README "Observability" section.
