# Tasks: observability-otel-lgtm

## 1. OpenTelemetry starter, export off by default (one commit)

- [ ] 1.1 Add `spring-boot-starter-opentelemetry` to api-gateway, auction-service and users-service. Set `spring.application.name` in each `application.yaml`, set the three export switches to `false`, and keep the correlation pattern on the console. Set `management.tracing.sampling.probability=1.0` in each module's test configuration. Verify, per service, a context test:
  - with defaults, no OTLP span, metric or log exporter bean exists, and the captured startup output contains no OTLP errors;
  - with the switches `true`, the exporter beans exist;
  - with the switches `true` and every OTLP endpoint set to `http://127.0.0.1:1` (nothing listening), a gateway request still succeeds within 1 s ("Collector down while export is enabled").
- [ ] 1.2 Verify a gateway test where a log line written during a request contains the request's trace id and span id.
- [ ] 1.3 Add `OpenTelemetryConfiguration` to each service (design decision 6). Verify, per service, a test with a `SimpleMeterRegistry`:
  - after one HTTP request, a timer `http.server.request.duration` exists and no `http.server.requests` timer exists;
  - the JVM meters carry the OTel `jvm.*` names (for example a thread-count meter under the OTel name).
  Confirm the exact JVM names against the Micrometer conventions at apply time.
- [ ] 1.4 Verify `./mvnw clean verify` is green, then commit.

## 2. Propagation over HTTP and Kafka (one commit)

- [ ] 2.1 Add `io.github.openfeign:feign-micrometer` to the gateway and users-service so Feign observation is active. Verify a gateway test with a mock HTTP server:
  - the request to auction-service carries `traceparent`, with the same trace id as the gateway's server span;
  - an incoming `traceparent` on the gateway request is continued.
- [ ] 2.2 Configure users-service's `ExchangeClient` without the observation capability, and with an interceptor that removes `traceparent`/`tracestate`. Verify a users-service test with a mock HTTP server standing in for apilayer: a call made inside a traced request arrives with neither header.
- [ ] 2.3 Enable `spring.kafka.listener.observation-enabled` in users-service. Verify an IT: a record published with a known `traceparent` header is settled in a span with that trace id, asserted through `micrometer-tracing-test`'s in-memory exporter.
- [ ] 2.4 Verify `./mvnw clean verify` is green, then commit.

## 3. Trace context through the outbox (one commit)

- [ ] 3.1 Add `V23__event_outbox_add_trace_parent.sql`. Store the current W3C trace context in `trace_parent` inside the finish transaction. Verify: an IT where finishing an auction under a known span leaves an outbox row whose `trace_parent` carries that trace id.
- [ ] 3.2 Make the relay open an `Observation` whose parent is extracted from `trace_parent`, in scope around `send(...).get()`, with `spring.kafka.template.observation-enabled=true`. For rows with a NULL `trace_parent`, open no extracted observation; they publish under the relay run's own trace. Verify these ITs:
  - with `@Scheduled` observation active, the send span's parent is the stored context, not the relay run;
  - the consumed record's `traceparent` header has the stored trace id;
  - an outbox row inserted before context start, simulating a restart, is published with its stored `traceparent`;
  - a row with a NULL `trace_parent` is published with a `traceparent` whose trace id differs from every stored `trace_parent` value (the relay run's trace).
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.

## 4. Log export (one commit)

- [ ] 4.1 Add `opentelemetry-logback-appender-1.0`, with its version pinned in the root `dependencyManagement` to the instrumentation release built on OpenTelemetry SDK 1.68.0 (not managed by Boot), and a `logback-spring.xml` that keeps Boot's console output and adds the OpenTelemetry appender, installed against the Boot `OpenTelemetry` bean. Verify a test with log export enabled and an in-memory log exporter: a log line written during a request is exported with the request's trace id.
- [ ] 4.2 Verify `./mvnw clean verify` is green, then commit.

## 5. Local Grafana stack and README (one commit)

- [ ] 5.1 Add `docker-compose.observability.yml`:
  - `lgtm` (`grafana/otel-lgtm:0.41.1`, a healthcheck, ports `127.0.0.1:3000:3000`, `127.0.0.1:4317:4317` and `127.0.0.1:4318:4318`);
  - for the three services, the export switches `true`, the OTLP endpoints at `http://lgtm:4318/v1/…`, sampling `1.0`, and `depends_on` `lgtm` healthy.
  Verify: `docker compose -f docker-compose.yml -f docker-compose.observability.yml config` shows loopback-only ports for `lgtm`.
- [ ] 5.2 Verify manually against the running stack:
  - place a bid through the gateway; in Grafana → Explore → Tempo, the trace shows api-gateway and auction-service spans;
  - after the auction finishes, one trace contains the finish job, the relay send and the users-service settlement;
  - Loki shows that trace's log lines;
  - `outbox.oldest.unpublished.age` can be queried.
  Record the trace ids checked in the commit body.
- [ ] 5.3 Add a README "Observability" section: the start command, the Grafana URL (loopback only), how to find a trace (from a log line's `traceId` or the Tempo search), and the note that export is off unless the override is used. Verify: following it on a clean clone works as written.
- [ ] 5.4 Verify `./mvnw clean verify` is green, then commit.
