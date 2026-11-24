# Spec Delta: database-migrations

## Purpose

Defines how auction-service and users-service bring their own PostgreSQL schema to the current version without manual steps.

## ADDED Requirements

### Requirement: Pending migrations are applied at startup
auction-service and users-service SHALL each apply every pending versioned migration of their own schema at startup, before accepting HTTP requests.

#### Scenario: Empty database
- **WHEN** users-service starts against an empty PostgreSQL database
- **THEN** all users-service migrations are applied before the HTTP port accepts requests
- **AND** the seeded accounts `test@admin.com` (ADMIN) and `test@user.com` (USER) exist

#### Scenario: Database already up to date
- **WHEN** a service restarts against a database that already has all migrations applied
- **THEN** no migration is re-applied and the service starts normally

### Requirement: A failed migration stops the service
A service SHALL NOT start serving requests when a migration fails or when an applied migration's checksum no longer matches its file.

#### Scenario: Edited migration detected
- **WHEN** a service starts and the checksum of an applied migration differs from the file on the classpath
- **THEN** startup fails and the HTTP port is never opened
