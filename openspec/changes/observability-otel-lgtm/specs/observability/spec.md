# Spec Delta: observability

## Purpose

Defines how requests and settlements can be followed across api-gateway, auction-service and users-service through distributed traces, correlated logs and metrics, and how a developer views them locally.

## ADDED Requirements

### Requirement: Traces cross HTTP service boundaries
A request handled by the gateway that calls auction-service or users-service SHALL produce spans in each service that share one trace id. The W3C `traceparent` header SHALL carry the trace context on every gateway → service call.

#### Scenario: Gateway calls a service
- **WHEN** a client sends `POST /api/v1/buyers/bids` to the gateway and the gateway calls auction-service
- **THEN** the outgoing request to auction-service carries a `traceparent` header
- **AND** the gateway span and the auction-service span have the same trace id

#### Scenario: Incoming trace context is continued
- **WHEN** a client sends a request to the gateway with a valid `traceparent` header
- **THEN** the gateway's span uses the trace id from that header

### Requirement: Trace context does not leave the system
Calls to external third-party services SHALL NOT carry `traceparent` or `tracestate` headers.

#### Scenario: Exchange-rate call
- **WHEN** users-service calls the external exchange-rate API while handling a traced request
- **THEN** the outgoing request has no `traceparent` and no `tracestate` header

### Requirement: Settlement continues the trace of the finish that caused it
When auction-service records an `AuctionFinished` event, it SHALL store the current trace context with the event. The published record SHALL carry it as a `traceparent` header. users-service SHALL process the record in a span of that same trace. This SHALL hold even when the event is published long after the finish, or after a restart.

#### Scenario: Finish and settlement in one trace
- **WHEN** an auction finishes and its event is later published and settled
- **THEN** the finish job's span, the relay's send span and the settlement span share one trace id

#### Scenario: Event published after a restart
- **WHEN** auction-service restarts between recording an event and publishing it
- **THEN** the published record still carries the `traceparent` stored at finish time

### Requirement: Logs are correlated with traces
Every log line that a service writes while handling a traced operation SHALL include the current `traceId` and `spanId`.

#### Scenario: Log line during a request
- **WHEN** the gateway logs a message while handling a request
- **THEN** the log line contains the request's trace id and the current span id

### Requirement: Telemetry export is opt-in and never affects requests
With export disabled (the default), services SHALL start and serve requests without trying to reach a collector. With export enabled and the collector unreachable, requests SHALL still succeed, and their latency SHALL NOT wait on export.

#### Scenario: Default configuration without a collector
- **WHEN** a service starts with default configuration and no collector is running
- **THEN** it starts normally and logs no OTLP export errors

#### Scenario: Collector down while export is enabled
- **WHEN** export is enabled and the collector is unreachable
- **THEN** gateway requests still succeed

### Requirement: Local Grafana shows traces, logs and metrics
Starting the system with the observability override SHALL make Grafana available at `http://localhost:3000` on the loopback interface only, with traces, logs and metrics from all three services.

#### Scenario: Following a bid in Grafana
- **WHEN** the stack runs with `docker-compose.observability.yml`, a bid is placed through the gateway, and the trace id is looked up in Grafana's trace view
- **THEN** the trace shows spans from api-gateway and auction-service

#### Scenario: Metrics from the outbox
- **WHEN** the stack runs with the observability override
- **THEN** the metric `outbox.oldest.unpublished.age` from auction-service can be queried in Grafana
