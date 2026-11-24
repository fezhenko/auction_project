# Proposal: outbox-publisher

Order: 9 of 13. Depends on: `runtime-startup` (Clock, duration), `testcontainers-postgres`, `auction-lifecycle-fixes` (atomic bids, winner in `buyer_id`) and `auction-finished-contract`.

## Why

Finishing an auction has to change its state and announce the result together. An announcement lost after the state change means money is never settled, and nothing would notice.
- Publishing to Kafka after the commit loses the event silently when the broker is down.
- Publishing inside the transaction can announce an auction that then rolls back.

Today's finish job also calls users-service through Feign inside its database transaction. That call fails, so every run rolls back.

## What Changes

- Kafka is added to `docker-compose.yml`: the `apache/kafka` image in KRaft mode, one node, no ZooKeeper.
- auction-service creates topic `auction.finished` (3 partitions) through a `NewTopic` bean.
- The producer uses `acks=all` with idempotence enabled, and bounded `max.block.ms` and `delivery.timeout.ms`.
- The finish job processes each due auction in its own transaction:
  - a conditional update `… WHERE auction_state = 'IN_PROGRESS'` that sets FINISHED and the final price;
  - when exactly one row changed, an insert of one outbox row, whose JSON payload snapshots the v1 event.
  The buyer is the auction's leading buyer (`buyer_id`), not any participant. Auctions with no bids finish as unsold.
- The finish job no longer calls users-service. Marking the item SOLD moves to `settlement-consumer`.
- A relay on its own scheduler thread:
  - claims unpublished rows with `FOR UPDATE SKIP LOCKED`;
  - sends each one keyed by auction id and waits for the broker's acknowledgement;
  - marks the row published.
  Rows published more than 7 days ago are purged. A gauge reports the age of the oldest unpublished row, and a warning is logged when it exceeds a threshold.

Deviations from brief:
- The brief asked for `BidPlaced` as well. It's dropped (decision E): nothing consumes it, and adding it to this outbox later is one change.
- Publishing goes through a transactional outbox (decision A). The brief left the mechanism open.

## Non-goals

- Consuming the event (`settlement-consumer`).
- The DLT, which belongs to the consumer and is created there.
- Exactly-once delivery. Delivery is at-least-once, and the consumer deduplicates.
- Debezium or other change-data-capture.

## Capabilities

### New Capabilities
- `event-publishing`: how a finished auction is recorded and announced reliably.

### Modified Capabilities
None. `auction-finished-event` from the previous change is used as-is.

## Impact

- `docker-compose.yml`: Kafka service.
- auction-service:
  - `spring-boot-starter-kafka` dependency;
  - producer and topic configuration;
  - `AuctionService.finishAuction` rewritten;
  - new outbox repository, relay and purge components;
  - new migration `V21__create_event_outbox.sql`;
  - `ItemClient` usage removed from finish.
- Test dependencies: `testcontainers-kafka` and the `commons` test-jar (fixtures). The payload is built from the shared `AuctionFinishedEvent` record, and the topic name comes from `Topics`.
- New properties: `outbox.relay.*` and `outbox.purge.*`.
