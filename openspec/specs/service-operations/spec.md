# service-operations Specification

## Purpose

Defines the operational HTTP surface every service exposes for health checking and identification, and what it must keep private.

## Requirements

### Requirement: Only health and info actuator endpoints are exposed
Each of api-gateway, auction-service and users-service SHALL expose only the `health` and `info` actuator endpoints over HTTP.

#### Scenario: Sensitive endpoints are not exposed on the internal services
- **WHEN** a client sends `GET /actuator/env`, `GET /actuator/beans`, `GET /actuator/configprops` or `GET /actuator/heapdump` to auction-service or users-service
- **THEN** the response is HTTP 404

#### Scenario: Sensitive endpoints are not exposed on the gateway
- **WHEN** a client authenticated as ADMIN sends `GET /actuator/env` or `GET /actuator/heapdump` to the gateway
- **THEN** the response is HTTP 404

### Requirement: Health and info are reachable without authentication
`GET /actuator/health` and `GET /actuator/info` SHALL succeed without credentials on every service, including the gateway, so that container healthchecks can use them.

#### Scenario: Anonymous health check on the gateway
- **WHEN** a client without a token sends `GET /actuator/health` to the gateway
- **THEN** the response is HTTP 200 with an overall `status` field

#### Scenario: Anonymous info on any service
- **WHEN** a client without a token sends `GET /actuator/info` to any service
- **THEN** the response is HTTP 200

#### Scenario: Other gateway paths still require authentication
- **WHEN** a client without a token sends `GET /api/v1/auctions` to the gateway
- **THEN** the request is rejected as unauthenticated

### Requirement: Health details are not disclosed
The health endpoint SHALL report the overall status without component details such as database URLs or broker addresses.

#### Scenario: Health response carries no component details
- **WHEN** a client sends `GET /actuator/health`
- **THEN** the response body contains no `components` or `details` entries
