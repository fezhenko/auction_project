# Tasks: auction-finished-contract

## 1. Event record, topics, schema and fixtures in commons (one commit)

- [ ] 1.1 Add `org.example.commons.kafka.Topics` (`AUCTION_FINISHED`, `AUCTION_FINISHED_DLT`) and the record `org.example.commons.event.AuctionFinishedEvent` with the v1 fields. A compact constructor rejects a half-set `buyerEmail`/`finalPrice` pair and a negative `finalPrice`. Verify: unit tests for the constructor checks.
- [ ] 1.2 Add `commons/src/main/resources/auction-finished/v1/schema.json` (draft 2020-12, `additionalProperties: true`, `if/then` for the `buyerEmail`/`finalPrice` pair, `finalPrice` 0..2147483647, `occurredAt` RFC 3339 with `Z`). Add the fixtures `sold.json` and `unsold.json` under `commons/src/test/resources/auction-finished/v1/`, and enable the `test-jar` goal. Verify: `./mvnw -pl commons package` produces the jar with the schema and a `-tests.jar` with the fixtures.
- [ ] 1.3 Add `AuctionFinishedContractTest`, which checks:
  - both fixtures validate;
  - a missing `auctionId` fails;
  - `finalPrice` 100.5 fails;
  - `finalPrice` 2147483648 fails;
  - `buyerEmail` with a null `finalPrice` fails;
  - the sold fixture plus an unknown field still validates, and deserializes into the record;
  - the record built from each fixture's values serializes to JSON equal to the fixture;
  - the schema's property set and types match the stored v1 property list.
  Verify: the test passes.
- [ ] 1.4 Verify `./mvnw clean verify` is green, then commit.

## 2. Contract documentation (one commit)

- [ ] 2.1 Add an "AuctionFinished v1" section to `commons/README.md` covering:
  - each field with its type, unit and nullability;
  - the topic name, record key, and the absence of type headers;
  - the additive-only rule and the new-topic rule for breaking changes;
  - that amounts are whole units with no currency.
  Verify: every statement matches `schema.json` and the record.
- [ ] 2.2 Verify `./mvnw clean verify` is green, then commit.
