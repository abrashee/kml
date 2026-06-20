# ADR-004: Standalone Microservice Runtime Topology

## Status
Accepted

## Context
KML started as a modular monolith with clear package-level bounded contexts. The next architectural step is to run the core domains as independently booted services that communicate through HTTP clients and brokered integration events.

The current bounded contexts are:

- User and access management
- Inventory and search
- Orders
- Shipments and WMS
- Warehousing
- Audit and observability

## Decision
KML will run the core backend domains as standalone Spring Boot service processes in the top-level `services/` workspace:

- `user-service`
- `order-service`
- `warehouse-service`
- `shipment-service`
- `inventory-service`
- `api-gateway`
- independently deployed admin and customer frontends
- shared infrastructure services for PostgreSQL, Redis, Kafka, RabbitMQ, OpenSearch, Prometheus, and Grafana

Each domain service has its own Maven module and Spring Boot entrypoint:

- `services/user-service` -> `com.kml.services.user.UserServiceApplication`
- `services/order-service` -> `com.kml.services.order.OrderServiceApplication`
- `services/warehouse-service` -> `com.kml.services.warehouse.WarehouseServiceApplication`
- `services/shipment-service` -> `com.kml.services.shipment.ShipmentServiceApplication`
- `services/inventory-service` -> `com.kml.services.inventory.InventoryServiceApplication`

Docker Compose builds each module through `services/Dockerfile` with a `MODULE` build argument. Each service starts as a standalone Java process with its own application name, service boundary, port, logs, metrics, tracing identity, and datasource URL.

Synchronous service-to-service calls use service URL properties under `kml.services.*.url`. Asynchronous communication uses RabbitMQ and Kafka integration events.

## Decision Drivers
- Avoid a high-risk rewrite.
- Make the requested service boundaries executable now.
- Keep each domain independently bootable and independently addressable.
- Make deployment, naming, observability, and communication boundaries explicit.
- Keep local development reproducible with Docker Compose.
- Preserve a path to separate Maven modules and database schemas without blocking local execution.

## Consequences
- Local infrastructure can run user, order, warehouse, shipment, and inventory as independent backend processes.
- Logs, metrics, traces, ports, and service names are separated per bounded context.
- HTTP and broker configuration now points at service DNS names rather than in-process calls.
- Each service has its own Maven module, application configuration, Docker build target, and bootstrap Flyway migration.
- The local PostgreSQL container creates separate databases for user, order, inventory, warehouse, and shipment.
- The legacy backend implementation has been retired from the active source tree.
- New backend functionality should be implemented inside the matching standalone service module, with cross-service coordination through gateway calls, service clients, or integration events.
