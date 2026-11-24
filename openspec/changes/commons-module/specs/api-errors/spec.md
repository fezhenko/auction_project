# Spec Delta: api-errors

## Purpose

Defines the single JSON body shape that api-gateway, auction-service and users-service return for every error response, so clients and the gateway can read errors the same way everywhere.

## ADDED Requirements

### Requirement: Errors use one body shape on every service
Every 4xx and 5xx response that api-gateway, auction-service or users-service produces from an exception handler SHALL have a JSON body with exactly these fields:
- `status`: the HTTP status code as an integer;
- `message`: a human-readable string.

#### Scenario: Validation error on users-service
- **WHEN** a client sends users-service a create-user request with an invalid email
- **THEN** the response is HTTP 400 with a body `{"status": 400, "message": …}`

#### Scenario: Business error on auction-service
- **WHEN** a client asks auction-service for an auction that does not exist
- **THEN** the response status and the body's `status` field are the same integer, and the body has a non-empty `message`

#### Scenario: Error raised in the gateway
- **WHEN** a buyer whose balance is lower than the amount bids through the gateway
- **THEN** the response is HTTP 400 with a body `{"status": 400, "message": …}`

### Requirement: Error bodies carry no internal details
Error bodies SHALL NOT include stack traces, exception class names or SQL text.

#### Scenario: Unexpected server error
- **WHEN** an unexpected exception reaches an exception handler in any service
- **THEN** the body contains only `status` and `message`, and `message` does not contain a stack trace, exception class name or SQL statement
