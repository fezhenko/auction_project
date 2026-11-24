# Proposal: readme-architecture

Order: 13 of 13. Depends on: every previous change.

## Why

The earlier changes each kept the README accurate for their own part. The README still needs a coherent top-level story: the new architecture (Kafka, outbox, relay, DLT), the stack, how to run it, and its delivery guarantees, worded exactly as the code behaves. Readers skim the README first, so it has to claim nothing the code doesn't do.

## What Changes

- Headline: an auction platform on Spring Boot 4 and Kafka, where a finished auction is recorded in an outbox in the same transaction and settled by users-service.
- Architecture diagram (Mermaid) showing:
  - client → gateway;
  - Feign calls to both services;
  - auction-service → outbox table → relay → `auction.finished` → users-service consumer;
  - unreadable or invalid messages → `auction.finished.DLT`;
  - both databases and the exchange-rate API.
- Module layout: `api-gateway`, `auction-service`, `users-service`, plus `commons`. `commons` holds the wire DTOs shared by the gateway and the services, the shared `ErrorDto`, the `AuctionFinished` event record with its v1 schema, and the topic names. It holds no entities, Feign clients or configuration classes.
- A sequence description of the money path: bid → finish (state + outbox row in one transaction) → relay → consume → deduplicated settlement (debit, credit, item SOLD in one transaction).
- Delivery wording, used verbatim: "At-least-once delivery; deduplicated on auctionId, so each auction is settled at most once; the dead-letter topic is not replayed automatically." The README never says "exactly-once", and never claims ordering.
- Tech stack: Java 21, Spring Boot 4.2, Spring Cloud 2026.0 (OpenFeign), Spring Data JDBC, PostgreSQL 17, Flyway, Kafka 4 (KRaft), Testcontainers 2, jjwt, MapStruct, Lombok, springdoc, Checkstyle/PMD/SpotBugs. Each version comes from the poms at the time of writing.
- Running: the one-command compose start, the IDE workflow, the E2E test, and the environment variables (`JWT_SECRET` as raw UTF-8 of at least 32 bytes, `EXCHANGE_API_KEY`).
- "Known limitations", rewritten to what is still true:
  - no currency model, and amounts are whole units;
  - DLT replay is manual;
  - auction-service and users-service have no authentication and rely on network isolation. auction-service trusts the email the gateway forwards;
  - the infrastructure ports (PostgreSQL, Kafka) are published on `127.0.0.1` for local development;
  - a REJECTED settlement (for example, insufficient funds) is recorded in users-service only. auction-service still shows the auction as finished with a buyer and final price;
  - a seller can buy their own item (debit and credit cancel out);
  - `TIMESTAMP` columns without a time zone, with UTC pinned at the JVM level;
  - OpenFeign is feature-complete upstream.
- "Roadmap" is replaced by "Possible next steps", or removed where everything shipped.
- One line on project history: settlement used to be a silent no-op, and this rework fixed it.

Deviations from brief: none. The brief's README item asked for exactly this.

## Non-goals

- Post-merge actions: adding the `kafka` and `testcontainers` repository topics, and updating the profile README. Those happen after merge, outside this PR.
- Docs for individual endpoints beyond the existing Swagger UI pointer.

## Capabilities

### New Capabilities
None. This is a documentation-only change (`skip_specs: true`).

### Modified Capabilities
None.

## Impact

- `README.md` only.
