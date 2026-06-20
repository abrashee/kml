# KML Standalone Services

This workspace contains the extracted microservice applications for KML.

## Modules

- `common` - shared DTOs, event contracts, and service configuration primitives.
- `api-gateway` - routes frontend/API traffic to the domain services.
- `user-service` - user, auth, role, and staff access boundary.
- `order-service` - order lifecycle boundary.
- `inventory-service` - stock and inventory search boundary.
- `warehouse-service` - warehouse and storage-unit boundary.
- `shipment-service` - shipment and WMS boundary.

## Build

Run from this directory:

```bash
./mvnw -DskipTests package
```

Build one service and its dependencies:

```bash
./mvnw -pl user-service -am -DskipTests package
```

## Runtime

The root Compose file builds these modules through `services/Dockerfile`.

```bash
docker compose --env-file infra/.env -f infra/docker-compose.microservices.yml --profile microservices up --build
```

Each service exposes:

- `/actuator/health`
- `/actuator/prometheus`
- `/api/v1/<domain>/service-info`

## Event Flow

Order fulfillment uses brokered events rather than a synchronous gateway chain:

1. `order-service` publishes `order.placed`.
2. `inventory-service` reserves stock and publishes `inventory.reserved`.
3. `shipment-service` creates the shipment.

Failed consumer messages are routed to durable RabbitMQ dead-letter queues. The current shipment address is an explicitly marked placeholder until an address is added to the order or user-profile contract.

## Verification

Run all service tests:

```bash
./mvnw test
```

Run the Compose-based cross-service critical path from the repository root:

```bash
bash scripts/microservice-integration-smoke.sh
```

The script boots the topology, creates warehouse and inventory data, places an order, and waits for the asynchronous shipment.
