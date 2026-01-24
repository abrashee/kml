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

## [0.2.0] - 24-01-2026

### Added
- Storage Unit Inventory Assignment entity + repository
- Inventory service support for storage unit & warehouse lookups
- Layout service & controller endpoints:
  - GET /api/v1/layouts/by-warehouse
  - GET /api/v1/layouts/by-storage-unit
- DTO mapping for layout responses