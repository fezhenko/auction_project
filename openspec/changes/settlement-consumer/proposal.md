# Proposal: settlement-consumer

Order: 10 of 14. Depends on: `outbox-publisher`.

## Why

Balance settlement has never worked. auction-service sends `BUYER`/`SELLER` and users-service expects `buyer`/`seller`. The update also runs in a read-only transaction, and `isPayed` is set to true anyway. Simply fixing the call would create a worse bug: a remote failure after users-service committed rolls back `isPayed`, and the buyer gets charged again on the next run. The item SOLD update shares the same flaw. users-service owns balances and items, so it should settle each finished auction itself, from the event, in one local transaction, at most once per auction.

## What Changes

- users-service consumes `auction.finished` in consumer group `users-service-settlement`. Each event is settled in one database transaction:
  - A row is inserted into `auction_settlements` keyed by `auction_id`, with `ON CONFLICT DO NOTHING`. A duplicate is acknowledged without effect.
  - An unsold auction is recorded as UNSOLD.
  - A sold auction runs, under a savepoint: debit the buyer if their balance covers the price; mark the item SOLD if it is ON_SELL and owned by the seller; credit the seller. These run in ascending user-id order. If any step can't apply, all of them roll back to the savepoint, and the settlement is recorded REJECTED with a reason: `INSUFFICIENT_FUNDS`, `ITEM_NOT_SELLABLE`, `UNKNOWN_USER` or `BALANCE_LIMIT`.
  - Rejections are recorded, never retried and never dead-lettered.
- Errors:
  - `ErrorHandlingDeserializer` plus schema validation. Unreadable or invalid messages go to `auction.finished.DLT` (3 partitions) without retry.
  - Transient failures are retried with bounded exponential backoff, then dead-lettered.
  - The DLT is not replayed automatically.
- Removed from auction-service:
  - the balance scheduler and its service methods;
  - the Feign `UserClient` and `ItemClient`, and the OpenFeign dependency (auction-service has no Feign client left);
  - the `UserType` enums.
- Removed from users-service: the unauthenticated `PATCH /api/v1/users/{id}/balance` and `UsersService.updateUserBalance`.
- **BREAKING:** `isPayed` is removed. That covers the `is_payed` column, the auction DTOs in auction-service and the gateway, auction-service `PATCH /api/v1/auctions/{id}/pay`, and the gateway's unused `AuctionsClient.setPayed`. No known client reads it.
- README: the settlement description and "Known limitations" are updated.

Deviations from brief:
- The brief only replaced the balance step. This change also moves the item SOLD update into the consumer (decision F), so the balance moves and the item state change commit together.
- It also removes `isPayed` (decision B), so users-service's settlement record is the single source of truth.

## Non-goals

- Automatic DLT replay or an admin UI for settlements.
- An `AuctionSettled` event back to auction-service.
- A currency model.

## Capabilities

### New Capabilities
- `balance-settlement`: how users-service turns a finished auction into balance changes and an item state change, at most once per auction.

### Modified Capabilities
None.

## Impact

- **users-service:**
  - `spring-boot-starter-kafka`;
  - consumer, error-handler and DLT configuration;
  - a new settlement service and repository;
  - `UsersController` and `UsersService` (endpoint removed);
  - migration `V6__create_auction_settlements.sql`;
  - test dependency `testcontainers-kafka` and the `commons` test-jar (fixtures); runtime dependency on a JSON Schema validator (the v1 schema ships in `commons`).
- **auction-service:**
  - scheduler, Feign clients and DTOs deleted;
  - `isPayed` removed from model, DTO, converter, repository, controller and tests;
  - migration `V22__drop_auctions_is_payed.sql`;
  - `services.users-service.url` and the balance cron property removed.
- **api-gateway:** `AuctionDto.isPayed` and `AuctionsClient.setPayed` removed, and tests updated.
- **API:** `isPayed` disappears from `GET /api/v1/auctions` (gateway and auction-service). `PATCH /api/v1/auctions/{id}/pay` and `PATCH /api/v1/users/{id}/balance` return 404.
