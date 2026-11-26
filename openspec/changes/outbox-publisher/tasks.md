# Tasks: outbox-publisher

## 1. Kafka infrastructure and topic (one commit)

- [ ] 1.1 Add Kafka (`apache/kafka`, KRaft combined mode, single node) to `docker-compose.yml`, with a healthcheck. Verify: `docker compose up -d` makes `kafka-topics.sh --bootstrap-server localhost:9092 --list` succeed.
- [ ] 1.2 Add `spring-boot-starter-kafka` to auction-service. Configure the producer: string key, raw JSON value, `acks=all`, `enable.idempotence=true`, `max.block.ms=5000`, `delivery.timeout.ms=30000`, no type headers. Add a `NewTopic` for `auction.finished` (3 partitions). Verify: an IT with a Testcontainers `KafkaContainer` (`@ServiceConnection`) finds the topic with 3 partitions after the context starts.
- [ ] 1.3 Verify `./mvnw clean verify` is green, then commit.

## 2. Finish with outbox (one commit)

- [ ] 2.1 Add `V21__create_event_outbox.sql`. Verify: an IT checks the table and partial index exist after migration.
- [ ] 2.2 Rewrite finish: bounded due-id selection, one transaction per auction, the conditional `UPDATE … RETURNING`, a winner email through `buyer_id`, and the snapshot payload inserted into `event_outbox`. Remove the `ItemClient` call, and remove `Auction.isDueToFinish`, which the SQL cutoff replaces. Verify: ITs for the three scenarios of "Finishing records the event atomically", with the failure injected by a constraint violation on insert.
- [ ] 2.3 Add a producer contract test: finish a seeded auction, read the outbox payload, validate it against the v1 schema from `commons`, and compare it with `sold.json` (and `unsold.json` for no bids) from the `commons` test-jar, ignoring `eventId` and `occurredAt`. Verify: the test passes.
- [ ] 2.4 Add ITs for "An auction finishes at most once" (two concurrent finish calls), "The buyer is the leading bidder" and "Event content is fixed at finish time". Verify: they pass.
- [ ] 2.5 Put the per-auction transaction in a separate bean or a `TransactionTemplate`. Add an IT with two due auctions, where recording the second auction's event fails. Verify: the first auction is FINISHED with its event recorded, and the second stays IN_PROGRESS. Also verify that a due IN_PROGRESS auction with `item_id` NULL (inserted directly) is not selected.
- [ ] 2.6 Verify `./mvnw clean verify` is green, then commit.

## 3. Relay (one commit)

- [ ] 3.1 Add the relay on a dedicated `ThreadPoolTaskScheduler`, with properties `outbox.relay.interval` (default `PT1S`), `outbox.relay.batch-size` (default 100) and `outbox.relay.stale-threshold` (default `PT5M`). Verify: an IT finishes an auction and consumes exactly one record from `auction.finished` with key = auction id, a value equal to the stored payload, and no `__TypeId__` header.
- [ ] 3.2 Add an IT for "Crash after recording, before publishing": insert an unpublished outbox row before the context starts, then start it and consume exactly one record. Verify: it passes.
- [ ] 3.3 Add an IT for "Broker unavailable at finish time" and "A broker outage does not stop auction scheduling": pause the Kafka container, finish and start auctions, check that their states changed, unpause, and consume the event. Verify: it passes.
- [ ] 3.4 Add an IT for "Crash after sending, before marking". A test hook makes the marking step fail after the broker acknowledged the send, so the transaction rolls back and the row stays unpublished. Then let the next relay run complete. Verify: two records with the same `eventId` are consumed, and the row ends up published.
- [ ] 3.5 Verify `./mvnw clean verify` is green, then commit.

## 4. Purge and staleness (one commit)

- [ ] 4.1 Add the daily purge (`outbox.purge.cron`, `outbox.purge.retention` default `P7D`). Verify: an IT with rows published 8 days and 6 days ago deletes only the 8-day row.
- [ ] 4.2 Add the gauge `outbox.oldest.unpublished.age` and the WARN log above the threshold. Verify: an IT with an unpublished row older than the threshold, with Kafka paused, finds the gauge value above the threshold and the warning in the captured log output.
- [ ] 4.3 Verify `./mvnw clean verify` is green, then commit.
