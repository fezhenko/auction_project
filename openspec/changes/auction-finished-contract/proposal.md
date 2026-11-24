# Proposal: auction-finished-contract

Order: 7 of 12. Depends on: `boot42-upgrade` (Jackson 3). It can land before or after `auction-lifecycle-fixes`.

## Why

Settlement does nothing today because producer and consumer disagreed on a string (`BUYER` vs `buyer`), and no test could notice. Before Kafka is wired, the `AuctionFinished` event needs a versioned contract that both sides test against: a schema plus golden fixtures. A mismatch then fails a build instead of silently moving no money. The contract also has to match the data: amounts are INTEGER whole units, there is no currency on auctions or balances, and the consumer needs the item id to mark the item SOLD.

## What Changes

- New Maven module `contracts`, resources only, used as a test dependency by auction-service and users-service. It holds:
  - `auction-finished/v1/schema.json`, a JSON Schema (draft 2020-12);
  - golden fixtures `sold.json` and `unsold.json`;
  - a test that validates the fixtures and the negative cases.
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
- `contracts/README.md` documents the fields, units and evolution rules.

Deviations from brief: none. Relative to the architect's T6, `finalPriceMinor` + `currency` become `finalPrice` in whole units with no currency (decision G), and `itemId` is added for decision F.

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

- New module `contracts` in the root pom's `<modules>`.
- Test-scoped dependency from auction-service and users-service, wired in the changes that use it.
- New test dependency: a JSON Schema validator (for example `com.networknt:json-schema-validator`).
