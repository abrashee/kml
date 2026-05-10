# ADR-001: Project Architecture and Module Structure

## Status
Accepted

## Context
KML models core supply chain and logistics workflows, including inventory, warehouses, orders, shipments, and users.  
The project begins as a **modular monolith** with a clear separation of concerns and is designed to evolve incrementally toward **event-driven architecture, microservices, and cloud-native deployment**.

The codebase is public and intended for **educational purposes**, so the architecture emphasizes:

- Clarity of design
- Testability and maintainability
- Observability and debuggability
- Safe public usage (no secrets or identifiable information)

A modular structure isolates responsibilities, allowing incremental evolution without breaking existing functionality.

## Decision
The project will adopt a **modular monolith architecture** with the following modules:

- **backend/** – Spring Boot application handling domain modeling, business logic, API exposure, and persistence coordination.
- **frontend/** – Two separate applications:
  - **React** (customer-facing)
  - **Angular** (admin-facing)  
  Each communicates via documented APIs, ensuring separation of concerns and independent development/deployment.
- **infra/** – Infrastructure assets including CI/CD pipelines, containerization, deployment manifests, and environment templates for dev, staging, and production.
- **docs/** – ADRs, module-level documentation, and project overview, with a general README linking all modules.

Each module maintains its own README detailing:

- Purpose
- Responsibilities
- Boundaries (what it owns and does not own)
- Design principles and notes

Modules are designed with **isolated responsibilities**, enabling incremental migration to microservices, event-driven architecture, and cloud-native deployment without disrupting existing functionality.

## Decision Drivers
- Maintainability and testability of code
- Clear module boundaries for incremental evolution
- Safe public exposure of code
- Observability and debuggability for educational purposes

## Consequences
- The modular monolith allows safe incremental evolution toward microservices.
- Responsibilities are explicit and easily reviewed.
- Public repository safety is enforced through placeholders and documentation.
- ADRs provide traceable reasoning for architectural decisions.
- Future developers can extend the project without breaking existing modules.
- Initial setup complexity is higher than a simple monolith.
- Requires careful management of module boundaries and API contracts.