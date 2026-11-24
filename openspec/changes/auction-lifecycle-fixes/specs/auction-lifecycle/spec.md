# Spec Delta: auction-lifecycle

## Purpose

Defines how a seller creates, stocks and schedules an auction through the gateway, how buyers join and bid, which state transitions are allowed, and how the gateway reports downstream rejections.

## ADDED Requirements

### Requirement: The gateway reports downstream rejections faithfully
When auction-service or users-service rejects a request with a 4xx status, the gateway SHALL return the same status and the downstream error message. When a downstream service fails with a 5xx status or cannot be reached, the gateway SHALL return HTTP 502.

#### Scenario: Downstream conflict
- **WHEN** a gateway request causes auction-service to answer HTTP 409
- **THEN** the gateway answers HTTP 409 with the downstream error message

#### Scenario: Downstream failure
- **WHEN** a gateway request causes auction-service to answer HTTP 500, or auction-service is unreachable
- **THEN** the gateway answers HTTP 502

### Requirement: Creating an auction creates exactly one auction
When an authenticated user sends `POST /api/v1/auctions` to the gateway, the system SHALL create exactly one auction in state PLANNED with that user as its seller, and SHALL return HTTP 201 with the auction, including its id.

#### Scenario: Successful creation
- **WHEN** an authenticated user sends `POST /api/v1/auctions`
- **THEN** the response is HTTP 201 with a body containing the new auction id and state `PLANNED`
- **AND** exactly one new auction exists for that seller

#### Scenario: Unauthenticated request
- **WHEN** a request without a valid token sends `POST /api/v1/auctions`
- **THEN** the request is rejected and no auction is created

### Requirement: Only the item's owner can put it up for auction
The system SHALL attach an item to an auction only when all of these hold:
- the caller is the auction's seller;
- the caller owns the item;
- the item is ON_SELL;
- the auction is PLANNED.

On success, the auction's start price SHALL equal the item price, and its minimal bid SHALL be 5% of that price.

#### Scenario: Owner attaches own item
- **WHEN** the seller of a PLANNED auction without an item sends `POST /api/v1/auctions/{auctionId}/item` with an item they own that is ON_SELL
- **THEN** the item is attached, the start price equals the item price, and the minimal bid is 5% of it

#### Scenario: Item owned by someone else
- **WHEN** the seller attaches an item owned by another user
- **THEN** the response is HTTP 403 and the auction is unchanged

#### Scenario: Item already sold
- **WHEN** the seller attaches an item they own whose state is SOLD
- **THEN** the response is HTTP 409 and the auction is unchanged

#### Scenario: Auction already running
- **WHEN** an item is attached to or replaced on an auction that is IN_PROGRESS or FINISHED
- **THEN** the response is HTTP 409 and the auction's item is unchanged

### Requirement: The seller schedules the auction start
The system SHALL let only the auction's seller set the start date through `PATCH /api/v1/auctions/{auctionId}/date`, and only while the auction is PLANNED, has an item attached, and the date is in the future.

#### Scenario: Seller schedules a future start
- **WHEN** the seller of a PLANNED auction with an item sets a start date one hour in the future
- **THEN** the response is HTTP 200 and the auction's start date is that value

#### Scenario: Someone else tries to schedule
- **WHEN** a user who is not the auction's seller sets its start date
- **THEN** the response is HTTP 403 and the start date is unchanged

#### Scenario: Date in the past
- **WHEN** the seller sets a start date in the past
- **THEN** the response is HTTP 400 and the start date is unchanged

#### Scenario: Auction without an item
- **WHEN** the seller sets a start date on an auction that has no item
- **THEN** the response is HTTP 409 and the start date is unchanged

#### Scenario: Auction already started
- **WHEN** the seller sets a start date on an auction that is IN_PROGRESS or FINISHED
- **THEN** the response is HTTP 409 and the start date is unchanged

### Requirement: A user joins an auction as a buyer
An authenticated user who is not the auction's seller SHALL be able to join a PLANNED or IN_PROGRESS auction through `POST /api/v1/buyers`, including before any bid exists. Joining SHALL be idempotent per (auction, user).

#### Scenario: Join before any bid
- **WHEN** a user joins an IN_PROGRESS auction that has no bids yet
- **THEN** the request succeeds and the user has one buyer record for that auction

#### Scenario: Seller tries to join own auction
- **WHEN** the auction's seller joins their own auction
- **THEN** the response is HTTP 409 and no buyer record is created

#### Scenario: Joining twice
- **WHEN** a user joins the same auction twice
- **THEN** both requests succeed and the user still has exactly one buyer record for that auction

#### Scenario: Joining a finished auction
- **WHEN** a user joins an auction that is FINISHED
- **THEN** the response is HTTP 409 and no buyer record is created

### Requirement: Auctions are finished only by the finish job
The auction state endpoint SHALL NOT move an auction to FINISHED, and SHALL NOT move it backwards (FINISHED to anything, or IN_PROGRESS to PLANNED).

#### Scenario: Request to finish through the state endpoint
- **WHEN** a client sends `PATCH /api/v1/auctions/{auctionId}/state` with status `FINISHED` to auction-service
- **THEN** the response is HTTP 400 and the auction state is unchanged

#### Scenario: Backwards transition
- **WHEN** a client requests state `PLANNED` for an auction that is IN_PROGRESS
- **THEN** the response is HTTP 400 and the auction state is unchanged

### Requirement: A bid is accepted atomically by one rule
A bid SHALL be accepted only if, at the moment it is applied, all of the following hold:
- the auction is IN_PROGRESS;
- amount ≥ start price;
- amount > current price;
- amount − current price ≥ minimal bid.

An accepted bid SHALL set the auction's current price to the amount and its leading buyer to the bidder in the same atomic step. A bid that fails the rule SHALL change nothing.

#### Scenario: Valid bid through the gateway
- **WHEN** a joined buyer whose balance covers the amount sends `POST /api/v1/buyers/bids` with 100 for an IN_PROGRESS auction with start price 100, current price 0 and minimal bid 5
- **THEN** the gateway answers with a 2xx status, the current price becomes 100, and the bidder becomes the leading buyer

#### Scenario: Balance lower than the bid
- **WHEN** a buyer whose balance is 50 bids 100 through the gateway
- **THEN** the response is HTTP 400 and nothing changes

#### Scenario: Increment below the minimal bid
- **WHEN** the current price is 100 and the minimal bid is 5, and a buyer bids 103
- **THEN** the bid is rejected with HTTP 400 and the current price and leading buyer are unchanged

#### Scenario: Auction not in progress
- **WHEN** a buyer bids on an auction that is PLANNED or FINISHED
- **THEN** the bid is rejected and nothing changes

#### Scenario: Concurrent equal bids
- **WHEN** two buyers bid the same valid amount on the same auction at the same time
- **THEN** exactly one bid is accepted, and the other is rejected with HTTP 409

#### Scenario: Bid races the finish job
- **WHEN** a bid and the finish job act on the same auction at the same time
- **THEN** either the bid is applied before the auction finishes, or the bid is rejected. A bid is never recorded as leading on a FINISHED auction

### Requirement: A user can take part in several auctions
The system SHALL resolve a bidder's buyer record by the pair (email, auction). The same user SHALL be able to join and bid in multiple auctions.

#### Scenario: Bidding in a second auction
- **WHEN** a user who joined auctions A and B as a buyer bids on auction B
- **THEN** the bid is attributed to their buyer record for auction B and no error occurs
