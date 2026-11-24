# Proposal: commons-module

Order: 6 of 14. Depends on: `boot42-upgrade` (Jackson 3, JSON round-trip tests) and `testcontainers-postgres`.

## Why

The gateway and the services each keep their own copy of the DTOs they exchange. About 30 class names exist on both sides of a gateway → service call. Nine are identical, and the rest have drifted: different property order, fields listed in one copy but not the other, different validation. Drift between two copies of a wire type is the same class of bug that kept settlement silently broken (`BUYER` vs `buyer`). Error bodies differ too, with three shapes for one concept: gateway `{errorMessage}`, auction-service `{message, status}`, users-service `{error}`. The gateway's planned 4xx pass-through (`auction-lifecycle-fixes`) needs one shape to forward.

## What Changes

- New Maven module `commons`: a plain jar with no Spring Boot repackaging, no auto-configuration, and no `application.yaml` or `db/migration` resources. The gateway and both services depend on it.
- The DTOs used on both sides of a gateway → service call move to `commons`. Each pair is merged into one type whose JSON shape matches what the service sends or accepts today. DTOs used by only one service stay where they are.
- **BREAKING (error body):** one shared `ErrorDto` `{ "status": <int>, "message": <string> }` replaces `ErrorMessageDto` (gateway) and both `ExceptionDto` classes. Every exception handler returns it.
- The JSON round-trip tests from `boot42-upgrade` prove the success payloads keep their field names and formats.

Owner decision on scope: `commons` holds DTOs, the error DTO, and (from `auction-finished-contract`) the event contract and topic names. It holds no persistence entities, no Feign client interfaces and no Spring configuration classes.

Deviations from brief: beyond the brief, which didn't mention shared code. It's the owner's decision, made while reviewing the proposals.

## Non-goals

- Sharing persistence entities (`Auction`, `Bid`, `Item`, …). Each service owns its schema.
- Sharing Feign client interfaces between the gateway and the service controllers. Spring Cloud OpenFeign advises against server/client interface sharing.
- Shared Spring `@Configuration` classes.
- Moving DTOs that `settlement-consumer` deletes later (`FinalPriceDto`, `UpdateBalanceResultDto`, auction-service's `UpdateItemDto`).
- Any change to success response shapes.

## Capabilities

### New Capabilities
- `api-errors`: the error response body every service returns.

### Modified Capabilities
None.

## Impact

- New module `commons` in the root `<modules>`, before the services.
- api-gateway, auction-service and users-service: their DTO packages shrink, and imports, MapStruct converters, Feign clients, controllers and tests are updated.
- All exception handlers (`GatewayExceptionHandler` and both `GlobalExceptionHandler` classes) return `ErrorDto`.
- Error JSON changes for clients: `errorMessage`/`error` become `message`, and `status` becomes a number on every service.
