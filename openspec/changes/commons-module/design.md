# Design: commons-module

## Context

- **DTOs that exist in both the gateway and a service** (by class name):
  - gateway ↔ auction-service: `AddItemToAuctionDto`, `AppUserDto`, `AuctionDto`, `AuctionItemDto`, `BuyerDto`, `CreateBidDto`, `CreateBuyerDto`, `CreateSellerDto`, `PriceDto`, `SellerDto`.
  - gateway ↔ users-service: `AddCurrencyDto`, `AppUserDto`, `CategoryDto`, `CreateItemDto`, `CreateUserDto`, `CredentialsDto`, `CurrencyDto`, `FullCurrencyInfoDto`, `ItemDto`, `UpdateCategoryDto`, `UpdateCategoryResultDto`, `UserVerificationDto`.
- **Where they stand:** about 9 are identical apart from the package. The rest differ in `@JsonPropertyOrder`, in validation annotations, or in fields; for example, the gateway `AuctionDto` declares `isPayed` but leaves it out of its property order.
- **Gateway-only wrappers** (`CreateBidDtoWithUserEmail`, `CreateBuyerWithUserEmailDto`) are the actual request types of some gateway → service calls.
- **Error bodies:** gateway `ErrorMessageDto {errorMessage}`, auction-service `ExceptionDto {message, status: HttpStatus enum name}`, users-service `ExceptionDto {error}`, used in about 40 places.
- **Lombok config:** the root `lombok.config` (Jackson 3 for `@Jacksonized`, from `boot42-upgrade`) applies to every module, including `commons`.

## Goals / Non-Goals

**Goals:**
- Exactly one Java type per wire shape exchanged between the gateway and a service.
- One error body (`specs/api-errors`).
- Success payloads unchanged.

**Non-Goals:**
- Entities, Feign interfaces and configuration classes in `commons` (owner decision).

## Decisions

1. **`commons` is a plain library.** It's a jar module whose `pom` depends only on these, with versions from the Boot BOM:
   - `jakarta.validation-api`
   - `com.fasterxml.jackson.core:jackson-annotations` (the annotations Jackson 3 still uses)
   - `tools.jackson.core:jackson-databind` (`provided`). Lombok `@Jacksonized` with `jacksonVersion += 3` generates `@JsonDeserialize`/`@JsonPOJOBuilder` from `tools.jackson.databind.annotation`. Every service already has databind at runtime.
   - `io.swagger.core.v3:swagger-annotations-jakarta`, if DTOs carry `@Schema`. It's the artifact springdoc brings, so the classes aren't on the classpath twice.
   - Lombok (`provided`)

   It has no Spring dependencies, so a service never gets beans or auto-configuration from it. It has no `application.yaml` and no `db/migration`, so decision D (no shared classpath roots) still holds. The quality gates run on it like any other module.
2. **Packages:**
   - `org.example.commons.dto.{auction,bid,buyer,seller,user,item,category,currency}`
   - `org.example.commons.error.ErrorDto`
   - `org.example.commons.event` and `org.example.commons.kafka` are added later by `auction-finished-contract`
3. **Which type is shared is decided per Feign call, not by class name.** For every gateway Feign method, the shared type is the service controller's request or response type, because the service defines the wire shape.
   - If a gateway type has fields the service never sends or accepts, they're dropped.
   - A gateway-only wrapper that is the real request body (such as `CreateBidDtoWithUserEmail`) becomes the shared type, and the service controller accepts it.
   - Client-facing gateway request DTOs that differ from the internal wire type stay in the gateway.
4. **Validation annotations travel with the shared DTO.** The service validates with `@Valid` as today. The gateway applies `@Valid` only where its controller already did, so the gateway doesn't start rejecting requests it used to forward.
5. **`ErrorDto { int status; String message; }` everywhere.** Exception handlers build it from the response status and the exception's message. Unexpected exceptions get a generic message (the detail is logged, not returned).
   - Rejected: keep three shapes. The gateway pass-through would have to understand all three.
   - Rejected: RFC 9457 `ProblemDetail`. It's the standard, and Spring 7 supports it natively. The owner chose a small shared DTO in `commons`. Switching later only changes the handler bodies.
6. **One commit per service area.** `commons` is created with `ErrorDto` first, then the DTOs move in groups. Each commit is green on its own, and the round-trip tests catch a shape change.

No Flyway migration is added by this change.

## Risks / Trade-offs

- [Every service must be rebuilt when a shared DTO changes] → Accepted. They live in one repo and are built and deployed together by compose and CI.
- [Merging two drifted copies changes a success payload] → The JSON round-trip tests from `boot42-upgrade` cover every gateway-facing DTO. A changed field name or format fails the build. Property order isn't significant in JSON and isn't asserted.
- [The error body change breaks a client that parses `errorMessage` or `error`] → No client exists in the repo. The change is marked BREAKING in the proposal and in the PR.
- [`commons` grows into a dumping ground] → `commons/README.md` states the scope rule: wire DTOs, the error DTO, the event contract and topic names only.
