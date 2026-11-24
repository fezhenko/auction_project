# Spec Delta: deployment

## Purpose

Defines how the complete system is started from a clean checkout, and which of its parts are reachable from outside the container network.

## ADDED Requirements

### Requirement: The whole system starts with one command
From a clean clone, with Docker available and a `.env` copied from `.env.example`, `docker compose --profile app up --build` SHALL build and start both databases, Kafka and the three services, and the gateway SHALL become healthy with no other manual step.

#### Scenario: Clean clone start
- **WHEN** a user clones the repository, copies `.env.example` to `.env`, and runs `docker compose --profile app up --build`
- **THEN** every service reports healthy
- **AND** `POST http://localhost:8080/api/v1/auth` with the seeded ADMIN account returns a token

### Requirement: Only the gateway is reachable from the host
In the `app` profile, auction-service and users-service SHALL NOT publish ports on the host. Among the application services, only the gateway SHALL be reachable, on host port 8080.

#### Scenario: Internal service port is closed
- **WHEN** the app profile is running and a client on the host connects to `localhost:8081` or `localhost:8082`
- **THEN** the connection is refused

### Requirement: Infrastructure ports are bound to the loopback interface
The database and Kafka ports that compose publishes for local development SHALL be bound to `127.0.0.1` only, so other machines on the network cannot reach them.

#### Scenario: Infrastructure ports from another interface
- **WHEN** compose is running in any profile
- **THEN** the published ports of `users-db`, `auction-db` and `kafka` are bound to `127.0.0.1` and not to `0.0.0.0`

### Requirement: Services start in dependency order
Each service SHALL start only after its database and Kafka report healthy, and the gateway SHALL start only after auction-service and users-service report healthy.

#### Scenario: Dependencies not yet healthy
- **WHEN** the app profile starts and Kafka is not yet healthy
- **THEN** auction-service and users-service have not started

### Requirement: Containers run without root privileges
Each service container SHALL run its process as a non-root user.

#### Scenario: Process user
- **WHEN** `id -u` is executed inside any running service container
- **THEN** the result is not `0`

### Requirement: Secrets come only from the environment
Service images SHALL NOT contain secret values. Secrets SHALL be supplied at runtime through environment variables, and the repository SHALL contain only placeholder values for them.

#### Scenario: Missing secret
- **WHEN** the app profile starts without `JWT_SECRET` in the environment
- **THEN** the gateway does not become healthy and its log names the missing variable
