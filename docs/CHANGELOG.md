# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 16-01-2026
### Added
- Inventory visibility endpoints (list, search, filter, get by SKU/ID)
- Quantity update endpoint (PATCH /api/v1/inventories/{sku}/quantity)
- DTO mapping for clean API responses
- Repository query methods for flexible search
- Modular backend structure (api, capacity, domain, infra, routing, simulation)
- CI pipeline skeleton (Maven build + tests + lint placeholders)
- Frontend skeletons (React + Angular)
- ADR and module documentation

## [0.2.0] - 21-01-2026
### Added
- Storage Unit Inventory Assignment entity + repository
- Inventory service support for storage unit & warehouse lookups
- Layout service & controller endpoints:
  - GET /api/v1/layouts/by-warehouse
  - GET /api/v1/layouts/by-storage-unit
- DTO mapping for layout responses

## [0.3.0] - 24-01-2026
### Added
- Order item add/remove support
- OrderStatus CRUD + lookup by name
- DTO mapping for clean API responses
- Proper service/controller separation
- Inventory lookup integration for order items
- Order endpoint: 
    - (POST /api/v1/orders)
    - (PUT /api/v1/orders/{id})
    - (GET /api/v1/orders)
    - (GET /api/v1/orders/{id})


## [0.4.0] - 25-01-2026
### Added
- Shipment creation endpoint (POST /api/v1/shipments)
- Shipment domain model with tracking ID generation
- Shipment persistence via repository and service layer
- Shipment response DTOs for safe API output
- Shipment retrieval endpoints:
  - GET /api/v1/shipments
  - GET /api/v1/shipments/{id}
  - GET /api/v1/shipments/by-status?status={status}
  - GET /api/v1/shipments/by-order?orderId={id} 


## [0.5.0] - 26-01-2026
### Added
- User domain model (User, UserRole)
- User persistence via UserRepository
- User creation endpoint (POST /api/v1/users)
- Password hashing using BCrypt
- Basic role-based authorization via AuthorizationService
- Temporary hardcoded user context for MVP (placeholder until later MVP)
- User activity log entity + repository + service (storage layer only, API postponed to later MVPs)