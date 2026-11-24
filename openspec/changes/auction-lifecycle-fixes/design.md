# Design: auction-lifecycle-fixes

## Context

- **Gateway create flow:**
  - `sellerClient.createNewSeller` → auction-service `POST /api/v1/sellers`, which returns `void`.
  - The V10 trigger `seller_insert_into_auction` inserts an auction for the new seller row.
  - The gateway then calls `auctionsClient.createAuction(seller.getSellerId())`, and gets an NPE.
- **Gateway lookups:** the gateway can't learn an auction's seller. `AuctionsClient.getSellerByAuctionId` (`GET /{id}/seller`) has no handler, `/sellers/{sellerId}` and `/sellers/{email}` are ambiguous, and `AuctionDto` carries no seller.
- **Gateway errors:** `GatewayExceptionHandler` maps no `FeignException`, so every downstream 4xx surfaces as 500.
- **Join:** gateway `POST /api/v1/buyers` → auction-service `BuyerService.createBuyer`. `isBuyerTheSeller(auction)` loads the buyer by `auction.getBuyerId()`, which is null before the first bid, then dereferences it (NPE). Nothing prevents a duplicate (auction, email) buyer.
- **Bids:**
  - Gateway `POST /api/v1/buyers/bids` → `BidService.makeBidToAuction`. It checks the balance and calls Feign, then always throws `UserBalanceIsNotEnoughException`, because there's no `return`.
  - auction-service `BidService.createBid` reads the auction, checks `amount > currentPrice`, inserts the bid, then runs an unconditional `updateBuyerIdForAuction`.
  - The insert fires V12 (`bids` → `buyers.bid_id`), which fires V13 (`buyers.bid_id` → a conditional update of `auctions.current_price` and `buyer_id`).
- **Buyers:** `buyers` rows are per auction (`buyers.auction_id`, `buyers.email varchar(30)`), but `findBuyerByEmail` ignores the auction.
- **Items:** `items.owner_id` references `users.user_id` in users-service. The gateway's `ItemDto` doesn't expose the owner. auction-service `addItemToAuction` already checks that the forwarded email equals the seller's email, but doesn't check the auction state.

## Goals / Non-Goals

**Goals:**
- The behavior in `specs/auction-lifecycle`, enforced in one place per rule.

**Non-Goals:**
- Fixing the unused, ambiguous seller lookups.

## Decisions

1. **Gateway error pass-through first.** A `@ExceptionHandler(FeignException.class)` returns `e.status()` and the downstream message for 4xx, and 502 for 5xx and for connection errors (`status() <= 0`). Every later gateway scenario depends on it.
2. **Drop the V10 trigger (`V18`) and keep the explicit create.** An explicit create that returns the auction is visible and testable.
   - Rejected: keep the trigger and have `POST /sellers` return the trigger-made auction. That keeps a hidden side effect in the schema.
3. **Seller checks live in auction-service, using the email the gateway forwards.** This is the pattern `addItemToAuction` already uses. Scheduling sends `{auctionDate, email}` to the extended `PATCH /api/v1/auctions/{id}/date`, which returns:
   - 403 when the email isn't the seller's;
   - 409 unless the auction is PLANNED with an item;
   - 400 for a past date.
   The gateway never needs to look up the seller.
   - Rejected: fix `GET /{id}/seller` and check in the gateway. That's an extra round trip and a second copy of the rule.
4. **Item ownership is checked in the gateway**, which can resolve both sides: the caller's user id from users-service `GET /api/v1/users/validate?email=`, and the item's `ownerId`, added to the shared `commons` `ItemDto` that users-service returns and the gateway reads. auction-service additionally rejects item attach or replace unless the auction is PLANNED (409). Every auction that reaches IN_PROGRESS therefore has an item.
5. **Join compares the joiner, and is idempotent.**
   - `createBuyer(email, auctionId)` compares `email` with the auction's seller email (409 on a match).
   - It inserts with `ON CONFLICT (auction_id, email) DO NOTHING`, backed by a unique index from `V19`, which also serves the (email, auctionId) lookup.
   - Joining is allowed while the auction is PLANNED or IN_PROGRESS. A FINISHED auction answers 409.
6. **One conditional UPDATE for bids, with the rule moved out of triggers (`V20` drops V13's `insert_new_bid_to_current_price`).**
   ```sql
   UPDATE auctions SET current_price = :amount, buyer_id = :buyerId, last_updated = now()
   WHERE auction_id = :auctionId AND auction_state = 'IN_PROGRESS'
     AND :amount >= start_price AND :amount > current_price
     AND :amount - current_price >= minimal_bid
   ```
   - The service reads the auction first and evaluates the rule on that read. If the rule fails there → 400.
   - If it passes, the service runs the UPDATE. One row means accepted, and the bid row is inserted in the same transaction. Zero rows means another bid or the finish job won between the read and the update → 409.
   - The status is decided from the pre-update read. Re-reading after a lost race would see the winner's price and wrongly report 400.
   - The finish job's `WHERE auction_state = 'IN_PROGRESS'` update in `outbox-publisher` takes the same row lock, so a bid and a finish serialize.
   - The V12 trigger stays; it only records the buyer's latest bid id.
   - Rejected: `SELECT … FOR UPDATE` then check. Correct, but the rule ends up split between SQL and Java.
   - Rejected: keep the triggers. Bids fail silently, and the service can't report why.
7. **The gateway bid path returns after a successful Feign call.** An insufficient balance stays a gateway-side 400.
8. **Status codes:**
   - 400: rule or validation failure.
   - 403: not owner or seller.
   - 409: state conflict, self-join or lost race.
   These map through the existing exception handlers in auction-service, and through Decision 1 in the gateway.

Flyway migrations (auction-service), numbered in the order their task groups land, so every intermediate commit migrates in version order:
- `V18__drop_seller_auction_trigger.sql` (group 2): drop trigger `seller_insert_into_auction` and its function.
- `V19__buyers_unique_auction_email.sql` (group 5): collapse duplicate (auction_id, email) buyer rows, then create a unique index on `buyers (auction_id, email)`.
  - Pick exactly one survivor per group: the row referenced by `auctions.buyer_id` if there is one, otherwise the lowest id.
  - Repoint `auctions.buyer_id`, `bids.buyer_id` and `chat.buyer_id` from the other rows to the survivor.
  - Then delete the other rows.
  The index build can't fail on leftovers, and no foreign key is left dangling.
- `V20__drop_bid_price_trigger.sql` (group 6): drop trigger `insert_new_bid_to_current_price` (on `buyers`) and its function.

## Risks / Trade-offs

- [Dropping V10 changes behavior for any other caller of `POST /sellers`] → Only the gateway create flow calls it. The integration test asserts one auction per create.
- [The `V19` dedupe deletes buyer rows in existing local databases] → There's no production data. References are repointed to the single survivor before deleting. An IT seeds duplicates, including a leader, bids and a chat row, and asserts the index exists and all references resolve.
- [auction-service trusts the forwarded email] → It's internal-only (`docker-compose-app` publishes no port for it), and that's stated as a known limitation.
- [The concurrency test is flaky] → Use a `CountDownLatch` with two threads against the real database, and assert on the final state, not on timing.
