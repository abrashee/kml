# Frontend

## Purpose
The frontend module contains all client-facing applications for KML.
Each frontend is implemented as an independent application and communicates with the backend via documented APIs.

## Applications
- React application: customer-facing interface
- Angular application: admin-facing interface

Each application maintains its own structure, state management, and test strategy.

## Responsibilities
- User interaction and presentation
- Client-side routing
- API integration with the backend
- Loading, error, and empty states
- Form validation and user feedback
- Accessibility and responsive design

## What This Module Does NOT Own
- Business rules or domain invariants
- Authentication logic beyond token handling
- Persistence or workflow orchestration

## Design Principles
- Single responsibility components
- Predictable state management
- Defensive API consumption (never assume success)
- Testability and isolation

## Notes
Frontend applications must treat all backend interactions as unreliable
and handle errors, retries, and failures explicitly.
