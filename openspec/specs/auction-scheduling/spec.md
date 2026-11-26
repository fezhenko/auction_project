# auction-scheduling Specification

## Purpose

Defines when a scheduled auction starts and when an auction in progress becomes eligible to finish, and how operators configure that timing.

## Requirements

### Requirement: A planned auction starts when its start time is reached
On each run of the start job, auction-service SHALL move every PLANNED auction whose start time is at or before the current UTC time to IN_PROGRESS, and SHALL leave every other auction unchanged.

#### Scenario: Start time has passed
- **WHEN** the start job runs and a PLANNED auction's start time is one minute in the past
- **THEN** that auction's state becomes IN_PROGRESS

#### Scenario: Start time is in the future
- **WHEN** the start job runs and a PLANNED auction's start time is one minute in the future
- **THEN** that auction stays PLANNED

#### Scenario: Auction without a start time
- **WHEN** the start job runs and a PLANNED auction has no start time
- **THEN** that auction stays PLANNED

### Requirement: Auction duration is configurable
An auction in progress SHALL become eligible to finish once the current UTC time is at or after its start time plus the configured auction duration. The default duration SHALL be 6 hours.

#### Scenario: Default duration not yet elapsed
- **WHEN** no duration is configured and an auction started 5 hours ago
- **THEN** the auction is not eligible to finish

#### Scenario: Short configured duration elapsed
- **WHEN** the duration is configured as 1 minute and an auction started 2 minutes ago
- **THEN** the auction is eligible to finish

### Requirement: Job schedules are configurable and evaluated in UTC
The start and finish job schedules SHALL be configurable through cron properties without a code change, and SHALL be evaluated in UTC regardless of the host time zone.

#### Scenario: Configured schedule is used
- **WHEN** the start job cron property is set to run every 5 seconds
- **THEN** the start job runs at that interval

#### Scenario: Host time zone does not shift decisions
- **WHEN** the service runs on a host whose default time zone is not UTC
- **THEN** start and finish decisions use the same UTC instants as on a UTC host
