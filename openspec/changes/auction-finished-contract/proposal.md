# Proposal: auction-finished-contract

Order: 8 of 13. Depends on: `commons-module`. It can land before or after `auction-lifecycle-fixes`.

## Why

Settlement does nothing today because producer and consumer disagreed on a string (`BUYER` vs `buyer`), and no test could notice. Before Kafka is wired, the `AuctionFinished` event needs one definition that both sides use, plus a wire-level contract (schema and golden fixtures) that both sides test against. A mismatch then fails a build instead of silently moving no money. The contract also has to match the data: amounts are INTEGER whole units, there is no currency on auctions or balances, and the consumer needs the item id to mark the item SOLD.

## What Changes

- In the `commons` module (from `commons-module`):
  - `org.example.commons.event.AuctionFinishedEvent`, a Java record used by the producer (auction-service) and the consumer (users-service);
  - `org.example.commons.kafka.Topics`, holding the constants `AUCTION_FINISHED = "auction.finished"` and `AUCTION_FINISHED_DLT = "auction.finished.DLT"`;
  - the JSON Schema (draft 2020-12) `auction-finished/v1/schema.json` as a main resource, because the consumer validates against it at runtime;
  - golden fixtures `sold.json` and `unsold.json` in `commons`' test resources, published as a `test-jar` that the services' tests depend on;
  - a test that validates the fixtures and the negative cases, and checks that the record serializes to the fixture shape and deserializes from it.
- v1 fields:
  - `eventId` (UUID)
  - `schemaVersion` (always 1)
  - `occurredAt` (UTC timestamp)
  - `auctionId`
  - `itemId`
  - `sellerEmail`
  - `buyerEmail` (nullable)
  - `finalPrice` (integer whole units, nullable)
- No `currency` field.
- Topic `auction.finished`. The record key is the auction id. No framework type headers.
- Evolution rule: changes are additive only, and consumers ignore unknown fields. A breaking change goes to a new topic (`auction.finished.v2`).
- `commons/README.md` gains a section on the event: fields, units and evolution rules.

Deviations from brief: none. Relative to the architect's T6:
- `finalPriceMinor` + `currency` become `finalPrice` in whole units with no currency (decision G);
- `itemId` is added for decision F;
- the contract lives in `commons` instead of a separate `contracts` module (owner decision).

## Non-goals

- Publishing (`outbox-publisher`) or consuming (`settlement-consumer`) the event.
- A schema registry or Avro.
- A `BidPlaced` event. It's dropped by decision E, because nothing would consume it.

## Capabilities

### New Capabilities
- `auction-finished-event`: the wire contract for the event announcing that an auction has finished.

### Modified Capabilities
None.

## Impact

- `commons`: the new packages `event` and `kafka`, the schema resource, the fixtures, and the `test-jar` goal of `maven-jar-plugin`.
- New test dependency in `commons`: a JSON Schema validator (for example `com.networknt:json-schema-validator`). It becomes a runtime dependency of users-service in `settlement-consumer`.
