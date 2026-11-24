# Spec Delta: event-publishing

## Purpose

Defines how auction-service finishes an auction and reliably announces the result on Kafka, so that every finished auction is eventually published at least once.

## ADDED Requirements

### Requirement: Finishing records the event atomically
When the finish job processes an IN_PROGRESS auction whose duration has elapsed, auction-service SHALL do the following in one database transaction:
- set the auction's state to FINISHED;
- set its final price to the leading bid, or to none when no bid was accepted;
- record exactly one `AuctionFinished` event.

If the transaction fails, none of these effects SHALL persist.

#### Scenario: Auction with a winning bid
- **WHEN** the finish job processes a due auction whose leading bid is 150 by buyer B
- **THEN** the auction is FINISHED with final price 150
- **AND** one recorded event exists with `buyerEmail` of B and `finalPrice` 150

#### Scenario: Auction without bids
- **WHEN** the finish job processes a due auction with no accepted bid
- **THEN** the auction is FINISHED with no final price
- **AND** one recorded event exists with `buyerEmail` null and `finalPrice` null

#### Scenario: Failure while recording the event
- **WHEN** recording the event fails inside the finish transaction
- **THEN** the auction remains IN_PROGRESS and no event is recorded

#### Scenario: One failing auction does not affect the others
- **WHEN** a finish run processes two due auctions and recording the second auction's event fails
- **THEN** the first auction is FINISHED with its event recorded, and the second remains IN_PROGRESS

### Requirement: An auction finishes at most once
auction-service SHALL record at most one `AuctionFinished` event per auction, even when finish runs overlap.

#### Scenario: Concurrent finish runs
- **WHEN** two finish runs process the same due auction at the same time
- **THEN** exactly one event is recorded for that auction

### Requirement: The buyer is the leading bidder
The event's `buyerEmail` SHALL be the email of the auction's leading buyer at finish time, not of any other participant.

#### Scenario: Several participants
- **WHEN** buyers A and B joined an auction and B holds the leading bid at finish
- **THEN** the event's `buyerEmail` is B's email

### Requirement: Event content is fixed at finish time
The published event SHALL carry the values recorded when the auction finished, even if auction data changes afterwards.

#### Scenario: Data changed after finish
- **WHEN** an auction's stored price is modified after its event was recorded but before it was published
- **THEN** the published event still carries the final price as of finish

### Requirement: Recorded events are published at least once
Every recorded event SHALL eventually be published to topic `auction.finished` once the broker is reachable. An event MAY be published more than once, with the same `eventId`.

#### Scenario: Broker unavailable at finish time
- **WHEN** an auction finishes while the broker is unreachable
- **THEN** the auction is FINISHED and its event is recorded
- **AND** the event is published after the broker becomes reachable

#### Scenario: Crash after recording, before publishing
- **WHEN** auction-service stops after committing a finish and before publishing its event
- **THEN** the event is published after the service restarts

#### Scenario: Crash after sending, before marking
- **WHEN** auction-service stops after the broker acknowledged an event but before marking it published
- **THEN** after restart the event is published again with the same `eventId`

### Requirement: A broker outage does not stop auction scheduling
The start and finish jobs SHALL keep running on schedule while the broker is unreachable.

#### Scenario: Jobs during a broker outage
- **WHEN** the broker is unreachable for longer than one finish interval
- **THEN** due PLANNED auctions still start and due IN_PROGRESS auctions still finish

### Requirement: Stalled publishing is visible
auction-service SHALL report the age of its oldest unpublished event as a metric, and SHALL log a warning on each relay run while that age exceeds the configured threshold.

#### Scenario: Events stuck beyond the threshold
- **WHEN** the oldest unpublished event is older than the threshold
- **THEN** a relay run logs a warning that includes the age in seconds
