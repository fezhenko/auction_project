# Proposal: auction-lifecycle-fixes

Order: 7 of 14. Depends on: `testcontainers-postgres` and `commons-module` (shared `ErrorDto`).

## Why

The core flow can't be driven through the gateway today, so the settlement work in later changes couldn't be demonstrated end to end. From reading the code:
- **Creating an auction:**
  - `POST /api/v1/auctions` always fails. auction-service `POST /api/v1/sellers` returns no body, so the gateway dereferences a null seller, and the NPE is swallowed as `CreateAuctionException`.
  - Even with that fixed, one request creates two auctions: the V10 trigger inserts one for every new seller, and the gateway then inserts a second.
- **Scheduling:** the gateway has no way to set an auction's start date, so auctions never start. It also has no working way to learn an auction's seller:
  - `AuctionsClient` calls `GET /{id}/seller`, which has no handler;
  - `/sellers/{email}` and `/sellers/{sellerId}` are ambiguous mappings.
- **Items:** nothing checks that the item put up for auction belongs to the caller, and an item can be swapped on an auction that is already running.
- **Joining** (`POST /api/v1/buyers`) throws before the first bid. The self-join check compares the current *leader* with the seller, and the leader is null until someone bids. The same user can also join the same auction twice.
- **Bidding:**
  - The gateway's bid path has no `return` after a successful bid, so every bid also answers "balance not enough".
  - auction-service checks before it acts, and isn't transactional.
  - A bid insert fires the V12 and V13 triggers, which apply the minimal-bid rule conditionally. The service then overwrites the price and leader unconditionally, so the rule doesn't hold.
  - The bidder is looked up by email alone, which throws once a user has joined two auctions.
- **State:** auction-service `PATCH /api/v1/auctions/{id}/state` accepts `FINISHED`, which skips finishing (and later, settlement).
- **Errors:** the gateway maps no Feign error, so every downstream 400 or 409 reaches the client as 500.

## What Changes

- **Gateway error pass-through:** downstream 4xx responses reach the client with the same status and error body; downstream 5xx and connection failures become 502.
- **Create:** creating an auction through the gateway creates exactly one auction and returns it. auction-service `POST /api/v1/sellers` returns the created seller, and the V10 seller-to-auction trigger is dropped.
- **Items:** putting an item up for auction requires that the caller owns it and that it is ON_SELL. Items can only be attached or changed while the auction is PLANNED.
- **Scheduling:** new gateway endpoint `PATCH /api/v1/auctions/{auctionId}/date`. The gateway forwards the caller's email. auction-service lets only the auction's seller set a future start date on a PLANNED auction that has an item.
- **Joining:** compares the *joining* user with the seller (409 on a self-join), works before any bid, and is idempotent per (auction, email), enforced by a unique index.
- **Bidding:**
  - A bid is applied as one atomic conditional update, using the rule the V13 trigger meant to enforce: the auction is IN_PROGRESS, amount ≥ start price, amount > current price, and amount − current price ≥ minimal bid.
  - The V13 trigger is dropped, and the bidder's buyer record is resolved per auction.
  - The gateway returns success after a placed bid.
- **State:** auction-service rejects `FINISHED` (and any backwards transition) on the state endpoint. Only the finish job finishes auctions.
- **README:** the overview describes the flow that now works.

Deviations from brief: beyond the brief (decision H). Without it, the brief's own flow, bid → auction finished → balance settled, can't be driven through the public API.

## Non-goals

- Finishing auctions and publishing events (`outbox-publisher`).
- Authentication inside auction-service. It stays internal-only, and its checks rely on the email the gateway forwards.
- Resolving the ambiguous `/sellers/{sellerId}` vs `/sellers/{email}` mapping. The gateway no longer needs either.
- Bid retraction, editing or listing history. Leaving an auction.

## Capabilities

### New Capabilities
- `auction-lifecycle`: creating, stocking, scheduling and joining auctions, the allowed state transitions, bid acceptance, and how the gateway reports downstream rejections.

### Modified Capabilities
None.

## Impact

- **API:**
  - `POST /api/v1/auctions` (gateway) returns 201 with the auction.
  - New `PATCH /api/v1/auctions/{auctionId}/date` (gateway).
  - `POST /api/v1/sellers` (auction-service) returns a body.
  - The state endpoint rejects `FINISHED`.
  - Downstream 4xx statuses pass through the gateway, and 5xx becomes 502.
  - A bid that loses a race gets 409.
  - A repeated join succeeds without creating a duplicate.
- Migrations (auction-service):
  - `V18__drop_seller_auction_trigger.sql`
  - `V19__buyers_unique_auction_email.sql`
  - `V20__drop_bid_price_trigger.sql`
- **Code:**
  - gateway: `GatewayExceptionHandler`, `AuctionService`, `BidService`, `AuctionController` and the Feign clients;
  - auction-service: `SellerController`/`SellerService`, `AuctionService` (state, date, item), `BuyerService`, `BidService` and `BuyerRepository`;
  - users-service: the item response gains the owner id.
