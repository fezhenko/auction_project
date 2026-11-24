# Tasks: observability-otel-lgtm

## 1. OpenTelemetry starter, export off by default (one commit)

- [ ] 1.1 Add `spring-boot-starter-opentelemetry` to api-gateway, auction-service and users-service. Set `spring.application.name` in each `application.yaml`, set the three export switches to `false`, and keep the correlation pattern on the console. Verify, per service, a context test:
  - with defaults, no OTLP span, metric or log exporter bean exists, and the captured startup output contains no OTLP errors;
  - with the switches `true`, the exporter beans exist.
- [ ] 1.2 Verify a gateway test where a log line written during a request contains the request's trace id and span id.
- [ ] 1.3 Verify `./mvnw clean verify` is green, then commit.

## 2. Propagation over HTTP and Kafka (one commit)

- [ ] 2.1 Make sure Feign observation is active in the gateway. Verify a gateway test with a mock HTTP server:
  - the request to auction-service carries `traceparent`, with the same trace id as the gateway's server span;
  - an incoming `traceparent` on the gateway request is continued.
- [ ] 2.2 Enable `spring.kafka.listener.observation-enabled` in users-service. Verify an IT: a record published with a known `traceparent` header is settled in a span with that trace id, asserted through `micrometer-tracing-test`'s in-memory exporter.
- [ ] 2.3 Verify `./mvnw clean verify` is green, then commit.

## 3. Trace context through the outbox (one commit)

- [ ] 3.1 Add `V23__event_outbox_add_trace_parent.sql`. Store the current W3C trace context in `trace_parent` inside the finish transaction. Verify: an IT where finishing an auction under a known span leaves an outbox row whose `trace_parent` carries that trace id.
- [ ] 3.2 Make the relay start its send span from the stored context, with `spring.kafka.template.observation-enabled=true`, and leave rows with a NULL `trace_parent` untouched. Verify these ITs:
  - the consumed record's `traceparent` header has the stored trace id;
  - an outbox row inserted before context start, simulating a restart, is published with its stored `traceparent`;
  - a NULL `trace_parent` publishes without the header.
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.

## 4. Log export (one commit)

- [ ] 4.1 Add `opentelemetry-logback-appender-1.0` and a `logback-spring.xml` that keeps Boot's console output and adds the OpenTelemetry appender, installed against the Boot `OpenTelemetry` bean. Verify a test with log export enabled and an in-memory log exporter: a log line written during a request is exported with the request's trace id.
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
