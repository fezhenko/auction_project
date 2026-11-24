# Tasks: auction-finished-contract

## 1. Contract module, schema and fixtures (one commit)

- [ ] 1.1 Add module `contracts` (packaging `jar`, resources only) to the root pom, holding `auction-finished/v1/schema.json` (draft 2020-12, `additionalProperties: true`, `if/then` for the `buyerEmail`/`finalPrice` pair, `finalPrice` 0..2147483647, `occurredAt` RFC 3339 with `Z`). Verify: `./mvnw -pl contracts package` puts the schema on the jar's classpath root.
- [ ] 1.2 Add fixtures `sold.json` and `unsold.json`. Verify: they appear in the jar.
- [ ] 1.3 Add `AuctionFinishedSchemaTest`, which checks:
  - both fixtures validate;
  - a missing `auctionId` fails;
  - `finalPrice` 100.5 fails;
  - `finalPrice` 2147483648 fails;
  - `buyerEmail` with a null `finalPrice` fails;
  - the sold fixture plus an unknown field still validates.
  Verify: the test passes.
- [ ] 1.4 Verify `./mvnw clean verify` is green, then commit.

## 2. Contract documentation (one commit)

- [ ] 2.1 Write `contracts/README.md` covering:
  - each field with its type, unit and nullability;
  - the topic name, record key, and the absence of type headers;
  - the additive-only rule and the new-topic rule for breaking changes;
  - that amounts are whole units with no currency.
  Verify: every statement matches `schema.json`.
- [ ] 2.2 Verify `./mvnw clean verify` is green, then commit.
