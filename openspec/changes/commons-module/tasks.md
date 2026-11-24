# Tasks: commons-module

## 1. Module and shared error body (one commit)

- [ ] 1.1 Add module `commons` (jar, API-only dependencies per design decision 1) to the root `<modules>` before the services, and add it as a dependency of api-gateway, auction-service and users-service. Add `commons/README.md` with the scope rule: wire DTOs, error DTO, event contract and topic names only; no entities, Feign clients or configuration classes. Verify: `./mvnw -pl commons verify` runs Checkstyle, PMD and SpotBugs on it, and `commons` has no `src/main/resources/application.yaml` or `db/migration`.
- [ ] 1.2 Add `org.example.commons.error.ErrorDto` (`int status`, `String message`). Switch every exception handler in the three services to it, and delete `ErrorMessageDto` and both `ExceptionDto` classes. Unexpected exceptions return a generic message. Verify, per service, a test for each `api-errors` scenario:
  - users-service invalid email → 400 `{status, message}`;
  - auction-service unknown auction → matching `status`;
  - gateway insufficient balance → 400 `{status, message}`;
  - an unexpected exception → a body with only `status` and `message`, and no class name or stack trace.
- [ ] 1.3 Update the README if it documents an error format. Verify: `grep -n errorMessage README.md` returns nothing.
- [ ] 1.4 Verify `./mvnw clean verify` is green, then commit.

## 2. Shared DTOs: auctions, bids, buyers, sellers (one commit)

- [ ] 2.1 For every gateway Feign method in `AuctionsClient`, `BidsClient`, `BuyerClient` and `SellerClient`, move the wire type the auction-service controller uses into `org.example.commons.dto.*`, following design decision 3. Delete the duplicates in the gateway and auction-service, and update imports, MapStruct converters and tests. Verify: no class with the same simple name and the same wire role remains in both modules (a check over the moved names).
- [ ] 2.2 Verify that the JSON round-trip tests from `boot42-upgrade` pass unchanged for every moved type. A test that changes is listed in the commit body with the reason.
- [ ] 2.3 Verify `./mvnw clean verify` is green, then commit.

## 3. Shared DTOs: users, items, categories, currencies (one commit)

- [ ] 3.1 Do the same for the gateway Feign methods in `UserClient` and `ItemClient` (users, items, categories, currencies, credentials and verification) against the users-service controllers. Verify the same name check as in 2.1.
- [ ] 3.2 Verify that the JSON round-trip tests pass unchanged for every moved type.
- [ ] 3.3 Verify `./mvnw clean verify` is green, then commit.
