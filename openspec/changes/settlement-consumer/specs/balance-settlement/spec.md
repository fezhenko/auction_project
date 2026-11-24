# Spec Delta: balance-settlement

## Purpose

Defines how users-service settles a finished auction: moving money between buyer and seller and marking the item sold, at most once per auction, with failures recorded or dead-lettered rather than lost.

## ADDED Requirements

### Requirement: Each auction is settled at most once
users-service SHALL apply the effects of at most one `AuctionFinished` event per `auctionId`. A later delivery for an already processed `auctionId` SHALL be acknowledged without any effect, whether it carries the same `eventId` or a different one.

#### Scenario: Same event delivered twice
- **WHEN** the same sold event is delivered twice
- **THEN** the buyer is debited once, the seller is credited once, and the item is marked SOLD once

#### Scenario: Different event for a settled auction
- **WHEN** an event with a new `eventId` arrives for an `auctionId` that was already processed
- **THEN** no balance or item changes and the event is acknowledged

### Requirement: A sold auction moves money and the item together
For a sold event, users-service SHALL do the following in one transaction:
- debit the buyer by `finalPrice`;
- credit the seller by `finalPrice`;
- set the item to SOLD;
- record the settlement as SETTLED.

Either all of these effects persist, or none do.

#### Scenario: Successful settlement
- **WHEN** a sold event arrives with `finalPrice` 150, the buyer's balance is 200, and the item is ON_SELL and owned by the seller
- **THEN** the buyer's balance is 50, the seller's balance has increased by 150, the item is SOLD, and the settlement is recorded SETTLED

### Requirement: Insufficient funds are recorded, not retried
When the buyer's balance is lower than `finalPrice`, users-service SHALL change no balance and no item, SHALL record the settlement as REJECTED with reason `INSUFFICIENT_FUNDS`, and SHALL acknowledge the event without retrying it or sending it to the dead-letter topic.

#### Scenario: Buyer cannot pay
- **WHEN** a sold event arrives with `finalPrice` 150 and the buyer's balance is 100
- **THEN** both balances are unchanged, the item stays ON_SELL, the settlement is REJECTED with `INSUFFICIENT_FUNDS`, and nothing is published to `auction.finished.DLT`

### Requirement: The item must be sellable by the seller
users-service SHALL settle only when the item is ON_SELL and owned by the seller named in the event. Otherwise it SHALL move no money and SHALL record the settlement as REJECTED with reason `ITEM_NOT_SELLABLE`.

#### Scenario: Item owned by someone else
- **WHEN** a sold event names an item that is owned by a different user than the seller
- **THEN** no balance or item changes and the settlement is REJECTED with `ITEM_NOT_SELLABLE`

#### Scenario: Item already sold
- **WHEN** a sold event names an item that is already SOLD
- **THEN** no balance changes and the settlement is REJECTED with `ITEM_NOT_SELLABLE`

### Requirement: Unknown participants are rejected
When `buyerEmail` or `sellerEmail` matches no user, users-service SHALL change nothing and SHALL record the settlement as REJECTED with reason `UNKNOWN_USER`.

#### Scenario: Buyer email unknown
- **WHEN** a sold event arrives whose `buyerEmail` matches no user
- **THEN** no balance or item changes and the settlement is REJECTED with `UNKNOWN_USER`

### Requirement: Balances stay within their range
A credit that would push the seller's balance above 2147483647 SHALL change nothing, and SHALL be recorded as REJECTED with reason `BALANCE_LIMIT`.

#### Scenario: Seller balance near the limit
- **WHEN** a sold event's credit would exceed the seller's maximum balance
- **THEN** no balance or item changes and the settlement is REJECTED with `BALANCE_LIMIT`

### Requirement: An unsold auction is recorded without effects
For an event with `buyerEmail` null, users-service SHALL record the settlement as UNSOLD and SHALL change no balance and no item.

#### Scenario: Auction without bids
- **WHEN** an unsold event arrives
- **THEN** the settlement is recorded UNSOLD and the item stays ON_SELL

### Requirement: Unreadable or invalid messages go to the dead-letter topic
A record that cannot be deserialized, or that does not conform to the v1 contract, SHALL be published unchanged to `auction.finished.DLT` with headers describing the failure. It SHALL NOT be retried, and consumption SHALL continue with the next record.

#### Scenario: Malformed JSON
- **WHEN** a record with value `{not json` arrives on `auction.finished`
- **THEN** it is published to `auction.finished.DLT` with its original key and value, and the next valid record is settled

#### Scenario: Contract violation
- **WHEN** a record arrives whose `finalPrice` is 100.5
- **THEN** it is published to `auction.finished.DLT` without retry and no settlement is recorded

### Requirement: Transient failures are retried with a bound
When settlement fails for a transient reason, such as the database being unreachable, users-service SHALL retry with exponential backoff up to a configured limit, and then SHALL publish the record to `auction.finished.DLT`. Dead-lettered records SHALL NOT be replayed automatically.

#### Scenario: Database briefly unavailable
- **WHEN** the database is unreachable for less than the retry budget while a sold event is consumed
- **THEN** the event is settled once the database is reachable again

#### Scenario: Retries exhausted
- **WHEN** the database stays unreachable beyond the retry budget
- **THEN** the record is published to `auction.finished.DLT` and is not processed again automatically

### Requirement: Settlement is the only way auction outcomes change balances
users-service SHALL NOT expose an HTTP endpoint that adjusts balances from auction outcomes. auction responses SHALL NOT carry a payment flag, and settlement state SHALL be owned by users-service.

#### Scenario: Removed balance endpoint
- **WHEN** a client sends `PATCH /api/v1/users/{id}/balance` to users-service
- **THEN** the response is HTTP 404 or 405 and no balance changes

#### Scenario: No payment flag on auctions
- **WHEN** a client sends `GET /api/v1/auctions` to the gateway
- **THEN** no auction in the response has an `isPayed` field
- **AND** `PATCH /api/v1/auctions/{id}/pay` on auction-service returns HTTP 404 or 405
