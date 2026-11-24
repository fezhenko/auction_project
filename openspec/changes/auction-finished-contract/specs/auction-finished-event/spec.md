# Spec Delta: auction-finished-event

## Purpose

Defines the wire contract of the event that auction-service emits when an auction finishes, which users-service relies on to settle balances and mark items sold.

## ADDED Requirements

### Requirement: Payload conforms to the v1 schema
Every `AuctionFinished` event value SHALL be a JSON object that validates against the v1 JSON Schema (`auction-finished/v1/schema.json`) and carries these fields:
- `eventId`: UUID string
- `schemaVersion`: the integer 1
- `occurredAt`: RFC 3339 timestamp in UTC with a `Z` suffix
- `auctionId`: integer ≥ 1
- `itemId`: integer ≥ 1
- `sellerEmail`: email string
- `buyerEmail`: email string or null
- `finalPrice`: integer or null

#### Scenario: Sold fixture validates
- **WHEN** the `sold.json` fixture is validated against the v1 schema
- **THEN** validation succeeds

#### Scenario: Unsold fixture validates
- **WHEN** the `unsold.json` fixture is validated against the v1 schema
- **THEN** validation succeeds

#### Scenario: Missing required field
- **WHEN** a payload without `auctionId` is validated
- **THEN** validation fails

### Requirement: Money is an integer in balance units
`finalPrice` SHALL be an integer between 0 and 2147483647 inclusive, in the same whole units as user balances. Floating-point values SHALL be invalid.

#### Scenario: Fractional amount rejected
- **WHEN** a payload with `finalPrice` 100.5 is validated
- **THEN** validation fails

#### Scenario: Amount above the balance range rejected
- **WHEN** a payload with `finalPrice` 2147483648 is validated
- **THEN** validation fails

### Requirement: Sold and unsold outcomes are explicit
An auction that finished with a leading buyer SHALL produce an event with non-null `buyerEmail` and `finalPrice`. An auction that finished without bids SHALL produce an event where both are null. A payload where exactly one of them is null SHALL be invalid.

#### Scenario: Unsold auction
- **WHEN** an auction finishes without any accepted bid
- **THEN** its event has `buyerEmail` null and `finalPrice` null

#### Scenario: Inconsistent outcome rejected
- **WHEN** a payload has a `buyerEmail` but a null `finalPrice`
- **THEN** validation fails

### Requirement: Contract evolves additively
v1 consumers SHALL ignore fields they do not know. Fields SHALL NOT be removed, renamed or retyped within v1. Such a change SHALL be published on a new topic.

#### Scenario: Unknown field is ignored
- **WHEN** a v1 consumer receives the `sold.json` fixture with an extra field `"note": "x"`
- **THEN** it processes the event exactly as it processes the fixture without that field

### Requirement: Record key and headers
Each event SHALL be published to topic `auction.finished`, with the record key set to the decimal string of `auctionId`. Records SHALL NOT carry framework type headers such as `__TypeId__`.

#### Scenario: Published record shape
- **WHEN** an event for auction 42 is published
- **THEN** the record key is `"42"`, the topic is `auction.finished`, and no `__TypeId__` header is present

### Requirement: Event identity is stable across redelivery
`eventId` and `occurredAt` SHALL be fixed when the event is recorded, and SHALL be identical in every delivery of that event.

#### Scenario: Redelivered event
- **WHEN** the same recorded event is delivered twice
- **THEN** both deliveries carry the same `eventId` and `occurredAt`
