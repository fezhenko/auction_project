# Tasks: settlement-consumer

## 1. Settlement logic in users-service (one commit)

- [ ] 1.1 Add `V6__create_auction_settlements.sql`. Verify: an IT finds the table and its check constraint after migration.
- [ ] 1.2 Implement the settlement service:
  - the outer transaction with the deduplication insert;
  - the nested savepoint with the three conditional updates, in ascending user-id order;
  - outcome and reason recording.
  Verify ITs, each driven by a `sold.json` or `unsold.json` fixture from `contracts`:
  - successful settlement;
  - same event twice;
  - different event for a settled auction;
  - insufficient funds;
  - item owned by someone else;
  - item already sold;
  - buyer email unknown;
  - seller balance near the limit;
  - auction without bids.
- [ ] 1.3 Add a concurrency IT: two settlements that trade in opposite directions between the same two users, run in parallel, both complete with correct balances. Verify: it passes 20 repeated runs locally.
- [ ] 1.4 Verify `./mvnw clean verify` is green, then commit.

## 2. Kafka consumer, errors and DLT (one commit)

- [ ] 2.1 Add `spring-boot-starter-kafka` and the consumer configuration from design decision 5, plus `NewTopic` beans for `auction.finished` and `auction.finished.DLT`. Verify: an IT with a Testcontainers `KafkaContainer` publishes `sold.json` to `auction.finished`, and the settlement is recorded SETTLED.
- [ ] 2.2 Add `ErrorHandlingDeserializer`, JSON parsing plus contract validation in the listener (any failure raises `InvalidEventException`), and a `DefaultErrorHandler` with the backoff and non-retryable classification, plus `DeadLetterPublishingRecoverer`. Verify ITs for:
  - malformed JSON (`{not json`) goes to the DLT after exactly one attempt, and the next valid record settles;
  - a contract violation goes to the DLT after exactly one attempt;
  - insufficient funds causes no DLT record.
- [ ] 2.3 Set a bounded pgjdbc `socketTimeout` and `connectTimeout` on the datasource. Add a test-only fault injector that throws `TransientDataAccessResourceException` a configured number of times. Verify these ITs:
  - "Database briefly unavailable": 2 injected failures, then the event settles once;
  - "Retries exhausted": failures beyond the retry budget send the record to `auction.finished.DLT`, and no settlement is recorded.
- [ ] 2.4 Verify `./mvnw clean verify` is green, then commit.

## 3. Remove the old settlement path (one commit)

- [ ] 3.1 Delete from auction-service:
  - `UpdateBalanceAfterAuctionFinishSchedule` and `AuctionService.updateUserBalance*`;
  - `UserClient`, `ItemClient` and their DTOs;
  - both `UserType` enums;
  - `@EnableFeignClients`, the OpenFeign dependency, `services.users-service.url` and the balance cron property.
  Verify: `grep -r FeignClient auction-service/src` returns nothing, and the module builds.
- [ ] 3.2 Delete users-service `PATCH /api/v1/users/{userId}/balance`, `UsersService.updateUserBalance` and exceptions that are no longer used. Verify: an IT where the PATCH returns 404 or 405.
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.

## 4. Remove `isPayed` (one commit, BREAKING)

- [ ] 4.1 Add `V22__drop_auctions_is_payed.sql`. Remove `isPayed` from the auction-service model, DTO, converter and repository, the `PATCH /api/v1/auctions/{id}/pay` endpoint, and the test builders (`AuctionUtil`). Verify: an auction-service IT where the auction JSON has no `isPayed` and `/pay` returns 404 or 405.
- [ ] 4.2 Remove `isPayed` from the gateway `AuctionDto` and `AuctionsClient.setPayed`, and update `AuctionControllerTest`. Verify: the gateway JSON round-trip test (from `boot42-upgrade`) is updated and passes without the field.
- [ ] 4.3 Verify `./mvnw clean verify` is green, then commit.

## 5. README (one commit)

- [ ] 5.1 Update the README so that:
  - the auction-service row no longer mentions a balance job or marking items;
  - the users-service row describes settlement from `auction.finished`;
  - "Known limitations" drops the settlement no-op and adds "dead-lettered events are not replayed automatically".
  Verify: each statement matches the code in this change.
- [ ] 5.2 Verify `./mvnw clean verify` is green, then commit.
