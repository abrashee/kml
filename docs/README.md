# KML – Logistics Simulation & Optimization Platform

## Overview
KML models core supply chain and logistics workflows, including inventory, warehouses, orders, shipments, and users. The backend starts as a modular monolith and evolves toward event-driven architecture, microservices, and cloud-native deployment. The emphasis is on clarity of design, correctness, testability, observability, and maintainability.

## Note & Disclaime
This project is intended **for educational and demonstrative purposes only**.  
It is **not a commercial product** and should **not be used in real-world logistics operations**.  
All examples, datasets, and configurations are synthetic, anonymized, and safe for public use.

This software is provided **“as-is”** under the [MIT License](../LICENSE), **without any warranty**.  
The author is **not liable for any damages or misuse** arising from use of this project.


## Project Goals
- Demonstrate backend and frontend engineering practices.
- Evolve architecture incrementally:
    - Monolith → Modular Monolith → Event-Driven → Microservices.
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
- Later stages include:
    - Event-driven communication.
    - Background processing.
    - Microservice boundaries.
    - Containerization and orchestration.

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
    - Testcontainers (database-backed tests)
    - Jest (React)
    - Karma / Jasmine (Angular)
- Infrastructure & Messaging (introduced progressively):
    - Docker
    - Kafka
    - Kubernetes
- Tools are added progressively, only when justified by project needs.

## Engineering Principles
- Separation of concerns and explicit module boundaries.
- Testability and maintainability by design.
- Observability and debuggability integrated from the start.
- Disciplined Git workflows and meaningful commit history.
- Documentation, including ADRs, to record architectural decisions.

## Integrated Module READMEs
For detailed context on each module, see their individual README.md files:
- [Backend README](../backend/README.md) – purpose, responsibilities, architecture, design principles.
- [Frontend README](../frontend/README.md) – purpose, applications, responsibilities, design principles.
- [Infrastructure README](../infra/README.md) – purpose, responsibilities, security, and operational guidance.

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

