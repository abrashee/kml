# ADR-002: Layered Architecture for Backend

## Status
Accepted

## Context
The backend module is responsible for domain modeling, business rules, API exposure, and persistence coordination.  
To keep the codebase maintainable and understandable, it is important to separate responsibilities into clearly defined layers.

Without explicit layering, the backend would quickly become tangled: controllers containing business logic, repositories used directly by controllers, and domain logic scattered across the codebase.

## Decision
The backend will follow a strict layered architecture:

- **API / Controller layer**
  - Handles HTTP requests, validation, and response mapping.
  - No business logic allowed.

- **Service / Domain layer**
  - Contains business logic and workflows.
  - Coordinates between repositories and domain entities.

- **Repository / Persistence layer**
  - Handles data access through JPA repositories.
  - No business logic allowed.

Dependencies flow **only downward** (Controller → Service → Repository).  
Upward dependencies are considered a violation and must be refactored.

## Decision Drivers
- Maintain clean separation of concerns.
- Enable easier unit testing of each layer.
- Prevent business logic leakage into controllers or repositories.
- Lay groundwork for future migration to microservices.

## Consequences
- Clear separation of concerns improves maintainability.
- Easier unit testing for each layer.
- Controllers remain thin and focused on API responsibilities.
- Business rules remain centralized in services and domain entities.
- Future migration to microservices becomes easier since boundaries are already defined.