# Design: outbox-publisher

## Context

- After `auction-lifecycle-fixes`, `auctions.buyer_id` points at the leading buyer, and bids update the auction with a conditional `UPDATE … WHERE auction_state = 'IN_PROGRESS'`.
- `runtime-startup` provides `Clock`, `auction.duration` and `isDue`.
- The event contract is fixed by `auction-finished-contract`.
- Spring's default task scheduler has one thread, which the start and finish jobs share today.

## Goals / Non-Goals

**Goals:**
- The behavior in `specs/event-publishing`, with at-least-once delivery.

**Non-Goals:**
- Ordering guarantees beyond what one event per auction gives. With a single event per auction, per-auction ordering is not a meaningful claim and is not made.

## Decisions

1. **Transactional outbox with a polling relay.**
   - Rejected: publish after commit. The event is lost when the broker is down.
   - Rejected: Kafka transactions. They don't cover the database write.
   - Rejected: Debezium. It adds Kafka Connect and a replication slot to operate.
   - Rejected: listen-to-yourself (publish first, update on consume). It inverts the source of truth for a money flow.
2. **Finish = bounded selection plus one transaction per auction.**
   - Select due ids: `WHERE auction_state = 'IN_PROGRESS' AND item_id IS NOT NULL AND auction_date <= :cutoff LIMIT :batch`, where `cutoff = now − duration` comes from the `Clock`. `auction-lifecycle-fixes` already makes an item mandatory before scheduling, and locks it once the auction leaves PLANNED. The `item_id IS NOT NULL` filter guards legacy rows, so a contract-invalid event (`itemId` is required) can never be recorded.
   - The per-auction transaction lives in a separate bean (`AuctionFinisher`), invoked from the job, or in a `TransactionTemplate`. `AuctionService` is `@Transactional` at class level, so a self-invoked method would join one outer transaction, and one failure would roll back the whole batch.
   - Then, per id, in its own transaction, run `UPDATE … SET auction_state = 'FINISHED', final_price = CASE WHEN buyer_id IS NULL THEN NULL ELSE current_price END, last_updated = now() WHERE auction_id = :id AND auction_state = 'IN_PROGRESS' RETURNING …`.
   - One row returned → join `sellers.email` and `buyers.email` through `seller_id` and `buyer_id`, build the v1 payload, and insert the outbox row.
   - Zero rows → another run or a race won, and nothing is done.
   - One failing auction never rolls back the others.
3. **Payload snapshot at insert.** `event_id` (UUID) and `occurred_at` (from the `Clock`) are generated once, and the JSON is stored as `jsonb`. The relay sends the stored bytes as they are, so later edits through the internal `/price` or `/state` endpoints can't change a recorded event.
4. **Relay on a dedicated single-thread `ThreadPoolTaskScheduler`.**
   - Each run (default every 1 s) opens a transaction and claims up to `batch` rows `WHERE published_at IS NULL ORDER BY id FOR UPDATE SKIP LOCKED`.
   - It sends each row, then calls `get()` on the send future with a bound below `delivery.timeout.ms`, and sets `published_at = now()`.
   - On the first send failure it stops the batch and commits what's already marked. Unsent rows stay unpublished.
   - The producer sets `max.block.ms` (5 s) and `delivery.timeout.ms` (30 s), so a down broker can't block the thread indefinitely, and the scheduling jobs never share this thread.
5. **`SKIP LOCKED` is kept** so a second instance would be safe. Ordering across instances isn't relevant with one event per auction, and compose runs one instance.
6. **Purge and staleness.**
   - A daily job deletes rows with `published_at < now − 7 days` (`outbox.purge.retention`, default `P7D`).
   - A Micrometer gauge `outbox.oldest.unpublished.age` (seconds) is registered.
   - A WARN log is written on each relay run while the age exceeds `outbox.relay.stale-threshold` (default `PT5M`).
   - Rejected: turn health DOWN. That would make Docker report the whole service unhealthy because of the broker, and actuator details are intentionally hidden (`boot42-upgrade`).
7. **Topic as a `NewTopic` bean.** `auction.finished` gets 3 partitions and replication factor 1, which suits one node.
8. **Kafka in compose:** `apache/kafka:4.4.0` (the version managed by Boot 4.2; confirm the tag at apply time) in KRaft combined mode, with a host listener on 9092 and an internal listener for containers. Tests use Testcontainers `org.testcontainers.kafka.KafkaContainer` with `@ServiceConnection`.

Flyway migration (auction-service): `V21__create_event_outbox.sql` creates `event_outbox`, with these columns:
- `id bigserial` primary key
- `event_id uuid` not null unique
- `topic text` not null
- `record_key text` not null
- `payload jsonb` not null
- `created_at timestamptz` not null, default `now()`
- `published_at timestamptz` null

It also adds a partial index on `(id) WHERE published_at IS NULL`.

## Risks / Trade-offs

- [Duplicate publication after a crash between ack and mark] → By design (at-least-once). `settlement-consumer` deduplicates on `auctionId`.
- [Relay throughput is one row at a time] → Fine at this scale. Batching sends and awaiting all futures is a follow-up if needed.
- [Between this change and `settlement-consumer`, nothing marks items SOLD] → It never worked (the Feign call failed and rolled back). It's stated in the PR, and the next change fixes it.
- [Clock skew between relay instances] → Not relevant: `published_at` uses database `now()`, and purge compares in the database.

## Migration Plan

`V21` is additive. Rollback means reverting the commits. `event_outbox` can stay, or be dropped by hand in a local environment.
