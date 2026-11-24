# Tasks: auction-lifecycle-fixes

## 1. Gateway error pass-through (one commit)

- [ ] 1.1 Add a `FeignException` handler to `GatewayExceptionHandler`: 4xx passes through with the downstream `ErrorDto` body (from `commons`), and 5xx or a connection error becomes 502. Verify: gateway tests with a mocked Feign client throwing 400, 403, 404, 409, 500 and a connection error get 400, 403, 404, 409, 502 and 502.
- [ ] 1.2 Verify `./mvnw clean verify` is green, then commit.

## 2. One auction per create (one commit)

- [ ] 2.1 Add `V18__drop_seller_auction_trigger.sql`. Make auction-service `POST /api/v1/sellers` return the created seller (201 with a body). Verify: an IT creates a seller and finds zero auctions for it.
- [ ] 2.2 Make the gateway create flow return the created auction with 201. Verify:
  - an auction-service IT where one create gives exactly one PLANNED auction;
  - a gateway test with mocked Feign clients where the response holds the auction id;
  - a gateway test where `POST /api/v1/auctions` without a token is rejected and no Feign call is made.
- [ ] 2.3 Verify `./mvnw clean verify` is green, then commit.

## 3. Items: ownership and auction state (one commit)

- [ ] 3.1 Expose the item owner id through the shared `commons` `ItemDto`, used by users-service and the gateway. Verify: a users-service IT where `GET /api/v1/items/{id}` includes `ownerId`.
- [ ] 3.2 In the gateway, reject attaching an item the caller doesn't own (403) or that isn't ON_SELL (409). In auction-service, reject item attach or replace unless the auction is PLANNED (409). Verify:
  - gateway tests for owner, not-owner and sold item;
  - an auction-service IT for "Owner attaches own item", asserting start price = item price and minimal bid = 5% of it;
  - an auction-service IT for "Auction already running".
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.

## 4. Scheduling the start (one commit)

- [ ] 4.1 Extend auction-service `PATCH /api/v1/auctions/{id}/date` to take `{auctionDate, email}` and return:
  - 403 when the email isn't the seller's;
  - 409 unless PLANNED with an item;
  - 400 for a past date.
  Verify: ITs for not-the-seller, past date, no item and already started.
- [ ] 4.2 Add gateway `PATCH /api/v1/auctions/{auctionId}/date`, which forwards the caller's email. Verify: gateway tests for success and for a downstream 403 passed through.
- [ ] 4.3 Verify `./mvnw clean verify` is green, then commit.

## 5. Joining an auction (one commit)

- [ ] 5.1 Add `V19__buyers_unique_auction_email.sql`: one survivor per duplicate group, references repointed, then the unique index. Verify: an IT applies the migrations up to V18, seeds duplicate buyer rows (one is the leader, with bids and a chat row), then applies V19. The index exists, one row per (auction, email) remains, and every `auctions`/`bids`/`chat` reference resolves.
- [ ] 5.2 Rewrite `BuyerService.createBuyer`:
  - compare the joining email with the seller's (409);
  - allow PLANNED and IN_PROGRESS, and answer 409 for FINISHED;
  - insert with `ON CONFLICT (auction_id, email) DO NOTHING`.
  Verify: ITs for join before any bid, a seller joining their own auction, joining twice, and joining a FINISHED auction.
- [ ] 5.3 Add `findBuyerByEmailAndAuctionId` and remove `findBuyerByEmail`. Verify: an IT where a user who joined two auctions bids in the second without error.
- [ ] 5.4 Verify `./mvnw clean verify` is green, then commit.

## 6. Atomic bids (one commit)

- [ ] 6.1 Add `V20__drop_bid_price_trigger.sql`. Replace `BidService.createBid` with one transaction:
  - resolve the buyer by (email, auctionId);
  - evaluate the rule on the read (400 on failure);
  - run the conditional `UPDATE`; zero rows gives 409;
  - insert the bid only when one row changed.
  Verify: ITs for the valid bid, the increment below the minimal bid, and an auction not in progress.
- [ ] 6.2 Add a `return` after a successful bid in the gateway `BidService`. Keep the insufficient balance as 400. Verify: gateway tests for "Valid bid through the gateway" (2xx) and "Balance lower than the bid" (400).
- [ ] 6.3 Add concurrency ITs: two equal concurrent bids give exactly one accepted and the other 409, and a bid racing a direct `UPDATE … SET auction_state='FINISHED' WHERE auction_state='IN_PROGRESS'` never leaves a leading bid recorded after the finish. Verify: both pass 20 repeated runs locally.
- [ ] 6.4 Verify `./mvnw clean verify` is green, then commit.

## 7. State transitions (one commit)

- [ ] 7.1 Make auction-service `PATCH /api/v1/auctions/{id}/state` reject `FINISHED` and backwards transitions with 400. Verify: ITs for both scenarios in "Auctions are finished only by the finish job".
- [ ] 7.2 Verify `./mvnw clean verify` is green, then commit.

## 8. README (one commit)

- [ ] 8.1 Update the README overview to describe the working flow: create an auction, attach an owned item, schedule the start, join, bid. Remove the matching "Known limitations" entries added in `baseline-build`. Verify: each sentence maps to an endpoint covered by a test in this change.
- [ ] 8.2 Verify `./mvnw clean verify` is green, then commit.
