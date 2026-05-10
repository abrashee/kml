# ADR-003: Design Pattern Selection

## Status
Accepted

## Context
The project aims for clarity, maintainability, and realistic engineering practices. Design patterns should be used only when they improve structure and readability.

Early use of patterns like Strategy, Factory, or Observer may add unnecessary complexity if the system does not yet require them.

## Decision
The project will use design patterns only when they are justified by actual needs.  
Initial allowed patterns include:

- **Repository pattern**
  - Implemented via Spring Data JPA repositories.
  - Keeps persistence concerns separated from business logic.

- **Service pattern**
  - Encapsulates business workflows and rules.
  - Keeps controllers thin.

- **Domain entities with invariants**
  - Domain logic (like quantity rules) lives inside entities.

Other patterns (Strategy, Factory, Observer, etc.) will be introduced later only when:
- the system complexity requires them, and
- they clearly improve maintainability or readability.

## Decision Drivers
- Avoid premature complexity.
- Keep codebase understandable and simple in early stages.
- Use patterns only when justified by real need.
- Ensure maintainability and readability remain the priority.

## Consequences
- Avoids premature complexity and unnecessary abstraction.
- Keeps early code simple and understandable.
- Enables pattern adoption later without refactoring major parts of the system.
- Encourages decisions based on real need rather than “pattern hunting.”