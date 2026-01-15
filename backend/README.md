# Backend

## Purpose
The backend module contains the KML backend application implemented as a Java Spring Boot modular monolith.
It is responsible for core domain modeling, business logic, API exposure, and persistence coordination.

This backend is intentionally designed to evolve over time from a well-structured monolith into an event-driven,
microservice-oriented architecture in later MVPs.

## Responsibilities
- Domain modeling for inventory, warehouses, orders, shipments, and users
- Business rules, workflows, and invariants
- REST API endpoints and request handling
- Input validation and error mapping
- Transactional consistency and persistence abstraction
- Application-level orchestration

## Architecture
The backend follows a strict layered architecture:

API / Controller Layer  
→ Service / Domain Layer  
→ Repository / Persistence Layer  

Dependencies are allowed in **one direction only** (top → bottom).
Any upward dependency is considered a design violation.

## What This Module Does NOT Own
- Frontend UI logic or state
- Infrastructure provisioning or deployment configuration
- Environment-specific settings or secrets

## Design Principles
- Separation of concerns
- Single responsibility per module
- Explicit dependencies
- Testability and readability over cleverness

## Notes for Reviewers
This backend is a learning-focused, CV-grade artifact.
Design decisions are documented explicitly using Architecture Decision Records (ADRs) located in `/docs/adrs`.
