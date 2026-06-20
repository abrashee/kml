# KML – Logistics Simulation & Optimization Platform

## Overview
KML models core supply chain and logistics workflows, including inventory, warehouses, orders, shipments, and users. The current backend is a standalone-service topology with an API gateway, database-per-service persistence, brokered domain events, distributed tracing, and independently buildable services.

## Note & Disclaime
This project is intended **for educational and demonstrative purposes only**.  
It is **not a commercial product** and should **not be used in real-world logistics operations**.  
All examples, datasets, and configurations are synthetic, anonymized, and safe for public use.

This software is provided **“as-is”** under the [MIT License](./LICENSE), **without any warranty**.  
The author is **not liable for any damages or misuse** arising from use of this project.


## Project Goals
- Demonstrate backend and frontend engineering practices.
- Demonstrate the completed transition from a modular monolith to independently running microservices.
- Practice and document:
    - Clean architecture and domain-driven design.
    - API design, validation, and security.
    - Testing strategies: unit, integration, system, and E2E.
    - Observability, logging, metrics, and debugging.
    - CI/CD workflows and Git discipline.
- Track and document design decisions using ADRs.

## High-Level Domain
- KML models common logistics concepts:
    - Inventory and stock management.
    - Warehouses and storage locations.
    - Orders and fulfillment lifecycle.
    - Shipments and delivery tracking.
    - User roles and access control.
    - Operational reporting and observability.
- Current runtime capabilities include:
    - RabbitMQ domain-event workflows.
    - Kafka stock-update events.
    - Containerized service and frontend deployment.
    - Kubernetes configuration, discovery, and external-secret templates.

## Technology Stack
- Backend:
    - Java + Spring Boot
    - PostgreSQL
- Frontend:
    - React (customer-facing UI)
    - Angular (admin-facing UI)
    - HTML5, CSS3, SASS
- Dev & Collaboration:
    - GitHub (version control, CI/CD)
    - Trello (roadmap and issue tracking)
    - Postman (API testing and exploration)
- Testing:
    - JUnit, Mockito (backend unit & integration tests)
    - Compose-based cross-service integration tests
    - Playwright frontend flow tests
- Infrastructure & Messaging:
    - Docker
    - RabbitMQ and Kafka
    - Kubernetes
    - Prometheus, Grafana, OpenTelemetry, and Jaeger

## Engineering Principles
- Separation of concerns and explicit module boundaries.
- Testability and maintainability by design.
- Observability and debuggability integrated from the start.
- Disciplined Git workflows and meaningful commit history.
- Documentation, including ADRs, to record architectural decisions.

## Integrated Module READMEs
For detailed context on each module, see their individual README.md files:
- [Services README](./services/README.md) – standalone microservice modules, gateway, and service build commands.
- [Frontend README](./frontend/admin-frontend/README.md) – frontend responsibilities and integration guidance.
- [Infrastructure README](./infra/README.md) – purpose, responsibilities, security, and operational guidance.

# Architecture Decision Records (ADRs)
This folder contains all ADRs documenting the major architectural and design decisions of the KML project.

## ADR List
1. [ADR-001: Project Architecture and Module Structure](./docs/adrs/ADR-001-modular-monolith.md)
2. [ADR-002: Layered Architecture for Backend](./docs/adrs/ADR-002-layered-architecture.md)
3. [ADR-003: Design Pattern Selection](./docs/adrs/ADR-003-design-pattern-selection.md)
4. [ADR-004: Standalone Microservice Runtime Topology](./docs/adrs/ADR-004-microservice-transition.md)

Each ADR contains:

- Status (Accepted / Proposed / Deprecated)
- Context
- Decision
- Decision Drivers
- Consequences


## Public Repository Safety
- No real credentials, secrets, tokens, or keys.
- No identifying machine paths, IPs, domains, or company data.
- All examples, configurations, and datasets are synthetic and safe for public use.

## Project Status
- In active development, proceeding incrementally through clearly defined MVPs.

## How to Explore
- Review module-level README files.
- Check ADRs for trade-offs and architectural decisions.
- Follow MVP milestones to understand system evolution.

## Author
- Abdul Rasheed Momand, developed the project to demonstrate system design, clean architecture, and engineering practices.
