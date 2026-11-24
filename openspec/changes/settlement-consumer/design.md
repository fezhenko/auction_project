# Design: settlement-consumer

## Context

- users-service owns `users` (`user_id`, `balance INTEGER`) and `items` (`id`, `owner_id → users.user_id`, `item_state` in `ON_SELL`/`SOLD`).
- `outbox-publisher` publishes v1 events to `auction.finished` (3 partitions, key = auction id) at least once.
- auction-service's `UpdateBalanceAfterAuctionFinishSchedule` (`@Async`, so runs overlap) and its Feign `UserClient`/`ItemClient` are the only users of `isPayed`, `PATCH /users/{id}/balance` and the item PATCH from finish.

## Goals / Non-Goals

**Goals:**
- The behavior in `specs/balance-settlement`.
- At-least-once consumption with deduplication on `auctionId`, so each auction is settled at most once.

**Non-Goals:**
- Exactly-once processing. Kafka transactions wouldn't cover the PostgreSQL write, and deduplication makes them unnecessary.

## Decisions

1. **Deduplicate on `auction_id` (primary key), not `event_id`.** The business invariant is one settlement per auction. `event_id` is stored for tracing.
2. **One outer transaction with a savepoint for the money moves.**
   - The outer transaction inserts the settlement row (`ON CONFLICT (auction_id) DO NOTHING`).
   - Zero rows → duplicate: return and acknowledge.
   - A nested transaction (`PROPAGATION_NESTED`, a JDBC savepoint) runs the conditional statements:
     - `UPDATE users SET balance = balance - :amt WHERE user_id = :buyer AND balance >= :amt`
     - `UPDATE items SET item_state = 'SOLD' WHERE id = :item AND owner_id = :seller AND item_state = 'ON_SELL'`
     - `UPDATE users SET balance = balance + :amt WHERE user_id = :seller AND balance <= 2147483647 - :amt`
   - Any of them updating zero rows throws a `SettlementRejected` with its reason. The savepoint rolls back, and the outer transaction sets `outcome = 'REJECTED'` and `reason`, then commits.
   - Rejected: pre-checking with `SELECT … FOR UPDATE`. It works, but it duplicates every rule in Java and SQL.
   - Rejected: compensating updates. More code, and easier to get wrong.
3. **Lock order.** The two balance updates run in ascending `user_id` order, whichever is buyer or seller. Concurrent settlements of opposite trades between the same two users then can't deadlock. If a deadlock still happens, PostgreSQL aborts one transaction, which is retried as a transient failure.
4. **Error classification with `DefaultErrorHandler`:**
   - Not retryable: `InvalidEventException` and `DeserializationException`. These go straight to the DLT.
     - The value is consumed as a `String`, so malformed JSON never fails in the Kafka deserializer. The listener parses it, and a JSON parse failure, a schema violation or a semantic violation against `contracts` v1 all raise `InvalidEventException`.
     - `ErrorHandlingDeserializer` still wraps the String and key deserializers, so even undecodable bytes reach the DLT instead of looping.
   - Retryable: everything else, with `ExponentialBackOffWithMaxRetries` (initial 1 s, multiplier 2, max interval 10 s, 5 retries), then the DLT.
   - Business rejections never throw out of the listener.
   - `DeadLetterPublishingRecoverer` targets `auction.finished.DLT`, on the same partition as the original.
5. **Consumer settings:**
   - `group.id=users-service-settlement`
   - `auto.offset.reset=earliest`, so the first start doesn't skip events
   - ack mode `RECORD`, offset committed after the database commit
   - concurrency 3
   - value deserialized as a String, validated against the schema, then mapped with unknown fields ignored
6. **Testing retries.** Pausing the PostgreSQL container doesn't exercise the retry path: pgjdbc has no socket timeout by default, so the listener would just block until unpause. The retry ITs use a test-only fault injector, a `@TestConfiguration` that wraps the settlement repository and throws `TransientDataAccessResourceException` a configured number of times. The production datasource sets a bounded `socketTimeout` (30 s) and `connectTimeout`, so a real outage also surfaces as a retryable exception.
7. **Topics.** users-service declares `NewTopic` beans for `auction.finished` (3 partitions; an idempotent duplicate of the producer's) and `auction.finished.DLT` (3 partitions). Start order between the services then doesn't matter.
8. **Removal of `isPayed`** happens in the same commit as the entity field removal, because Spring Data JDBC selects every mapped column.

Flyway migrations:
- users-service `V6__create_auction_settlements.sql` creates `auction_settlements`, with these columns:
  - `auction_id bigint` primary key
  - `event_id uuid` not null
  - `outcome text` not null, check in (`SETTLED`, `UNSOLD`, `REJECTED`)
  - `reason text` null
  - `item_id bigint` not null
  - `buyer_user_id bigint` null
  - `seller_user_id bigint` null
  - `amount integer` null
  - `processed_at timestamptz` not null, default `now()`
- auction-service `V22__drop_auctions_is_payed.sql`: `ALTER TABLE auctions DROP COLUMN is_payed`.

## Risks / Trade-offs

- [A crash between the database commit and the offset commit redelivers the event] → The deduplication row makes the redelivery a no-op.
- [The DLT grows unnoticed] → It's documented as manual. The README tells operators how to inspect it with `kafka-console-consumer.sh`.
- [Removing `isPayed` breaks an unknown client] → None was found in the repo or its UI folder. It's flagged as BREAKING in the PR.
- [Self-purchase: buyer equals seller] → The debit and credit cancel out, and the item becomes SOLD. That's accepted, and preventing self-bids is left to the gateway as a follow-up.

## Migration Plan

`V6` is additive. `V22` drops a column whose data is meaningless (it was set without settling). Rollback means reverting the commits. `V22` can't be undone without a new migration, which is acceptable here.
