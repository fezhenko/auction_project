# Design: auction-finished-contract

## Context

- Money columns `auctions.final_price`, `users.balance` and `items.price` are `INTEGER`. The Java code carries `Double`, and PostgreSQL rounds on insert.
- Only items carry a `currency_id`; auctions and balances have none.
- auction-service knows buyers and sellers by email, and users-service resolves emails to users.
- Two modules can't share relative resource paths cleanly, and `D` rules out a shared test JVM.

## Goals / Non-Goals

**Goals:**
- One schema and one set of fixtures that both services test against.

**Non-Goals:**
- Runtime schema validation on the producer. The producer test asserts conformance; the consumer validates at runtime in `settlement-consumer`.

## Decisions

1. **JSON Schema plus golden fixtures in a resources-only module.**
   - Rejected: Avro with a schema registry. That's extra infrastructure for one event.
   - Rejected: sharing a Java DTO jar. It couples the services' code and hides wire-level drift, such as the exact JSON field names.
   - Rejected: relative-path resources. They're fragile across modules and IDEs.
2. **A nullable pair instead of an `outcome` enum.** `buyerEmail` and `finalPrice` are both null or both set, enforced with `if/then` in the schema. Adding an enum value later would break strict consumers, while adding a nullable field doesn't.
3. **`finalPrice` as an integer in whole units, with no currency.** This matches the INTEGER columns. The upper bound 2147483647 matches `users.balance` (int4). A currency can be added as an additive optional field when balances get one.
   - Rejected: `finalPriceMinor` with `currency`. The name would claim minor units the data doesn't have, and there's no currency to send.
4. **Emails as participant identity.** They're what auction-service has. Resolving to user ids is the consumer's job, and unknown emails become a recorded rejection (see `settlement-consumer`).
5. **No type headers.** The consumer deserializes into its own type, so the producer's class name never couples the services.
6. **The topic name has no version suffix for v1** (`auction.finished`). A breaking change gets `auction.finished.v2`, and the DLT is `auction.finished.DLT`.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [The schema and the Java producer drift apart] → `outbox-publisher` adds a producer test that serializes a real finished auction and validates it against the schema. `settlement-consumer` adds a consumer test that consumes both fixtures.
- [Emails change for a user between finish and settlement] → Accepted. The window is seconds, and the consumer records it as an unknown user instead of guessing.
