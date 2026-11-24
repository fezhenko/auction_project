# Design: auction-finished-contract

## Context

- Money columns `auctions.final_price`, `users.balance` and `items.price` are `INTEGER`. The Java code carries `Double`, and PostgreSQL rounds on insert.
- Only items carry a `currency_id`; auctions and balances have none.
- auction-service knows buyers and sellers by email, and users-service resolves emails to users.
- `commons` exists from `commons-module` as a plain library shared by all services.

## Goals / Non-Goals

**Goals:**
- One Java definition of the event, and one wire contract (schema and fixtures) that both sides test against.

**Non-Goals:**
- Schema validation on the producer at runtime. The producer test asserts conformance; the consumer validates at runtime in `settlement-consumer`.

## Decisions

1. **A shared record plus a wire contract, both in `commons`.**
   - The record removes hand-copied types on the two sides.
   - The JSON Schema and golden fixtures stay too. A shared class changes both sides together, so tests stay green, but events already in Kafka (or produced by an older deployment) would still break. The fixtures pin the wire format independently of the Java type, so a field rename fails the fixture tests.
   - Rejected: Avro with a schema registry. That's extra infrastructure for one event.
   - Rejected: the schema and fixtures alone, in a resources-only module. Both services would still hand-write the type, which is how `BUYER`/`buyer` happened.
2. **The schema is a main resource and the fixtures are test-only.** The consumer loads the schema at runtime. The fixtures ship in a `test-jar` (`maven-jar-plugin:test-jar`) that the services depend on with `<type>test-jar</type><scope>test</scope>`, so production jars don't carry test data.
3. **A nullable pair instead of an `outcome` enum.** `buyerEmail` and `finalPrice` are both null or both set, enforced with `if/then` in the schema and in the record's compact constructor. Adding an enum value later would break strict consumers, while adding a nullable field doesn't.
4. **`finalPrice` is an `Integer` in whole units, with no currency.** This matches the INTEGER columns. The upper bound 2147483647 matches `users.balance` (int4). A currency can be added as an additive optional field when balances get one.
   - Rejected: `finalPriceMinor` with `currency`. The name would claim minor units the data doesn't have, and there's no currency to send.
5. **Emails as participant identity.** They're what auction-service has. Resolving to user ids is the consumer's job, and unknown emails become a recorded rejection (see `settlement-consumer`).
6. **No type headers, and names come from `Topics`.** The consumer deserializes into the shared record explicitly, so no class name travels on the wire. Topic names are constants in one place.
7. **The topic name has no version suffix for v1** (`auction.finished`). A breaking change gets `auction.finished.v2`, and the DLT is `auction.finished.DLT`.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [The JSON Schema validator may still be built on Jackson 2] → Check at apply time. If it is, users-service accepts Jackson 2 at runtime for validation only (Boot 4.2 still manages `jackson-2-bom`). Otherwise pick a validator built on Jackson 3. Either way, the validator stays out of `commons`' compile classpath.

- [Someone edits the shared record and the fixtures together, and breaks the wire] → The `commons/README.md` evolution rule (additive only), plus a test that fails when a v1 schema property is removed or retyped. That's a stored copy of the v1 property list, compared against the schema.
- [The schema and the Java record drift apart] → A `commons` test serializes the record built from each fixture's values and compares it with the fixture JSON, then deserializes each fixture into the record.
- [Emails change for a user between finish and settlement] → Accepted. The window is seconds, and the consumer records it as an unknown user instead of guessing.
