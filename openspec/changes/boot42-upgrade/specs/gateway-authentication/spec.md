# Spec Delta: gateway-authentication

## Purpose

Defines how the API gateway obtains, validates and protects the key it uses to sign and verify JSON Web Tokens for client authentication.

## ADDED Requirements

### Requirement: Signing key strength is enforced at startup
The gateway SHALL refuse to start when `JWT_SECRET` is missing or empty, or when its UTF-8 encoding is shorter than 32 bytes.

#### Scenario: Secret shorter than 32 bytes
- **WHEN** the gateway starts with a `JWT_SECRET` whose UTF-8 encoding is 31 bytes
- **THEN** startup fails before the HTTP port is opened
- **AND** the error message names `JWT_SECRET` and the minimum length

#### Scenario: Missing secret
- **WHEN** the gateway starts without `JWT_SECRET` set
- **THEN** startup fails with an error that names `JWT_SECRET`

#### Scenario: Secret of sufficient length
- **WHEN** the gateway starts with a `JWT_SECRET` whose UTF-8 encoding is at least 32 bytes
- **THEN** the gateway starts and accepts logins at `POST /api/v1/auth`

### Requirement: Signing key is the raw UTF-8 secret
The gateway SHALL sign and verify tokens with HMAC-SHA256, using the UTF-8 bytes of `JWT_SECRET` as the key without any decoding.

#### Scenario: Token issued by the gateway is accepted
- **WHEN** a user logs in at `POST /api/v1/auth` and calls a protected endpoint with the returned token
- **THEN** the request is authenticated as that user

#### Scenario: Token signed with a different key is rejected
- **WHEN** a client calls a protected endpoint with a token signed by any other key
- **THEN** the request is rejected and the protected endpoint is not invoked

### Requirement: Secret value is never disclosed
The gateway SHALL NOT write the value of `JWT_SECRET` to logs, error messages or HTTP responses.

#### Scenario: Startup failure does not echo the secret
- **WHEN** startup fails because the secret is too short
- **THEN** neither the log output nor the error message contains the secret value
