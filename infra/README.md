# Infrastructure Module

## Overview
The infra module contains all infrastructure and operational assets required to build, test, and deploy the KML system. This includes CI/CD workflows, containerization, deployment manifests, and environment configuration templates. The module is designed to support backend and frontend applications without embedding business logic.

## Responsibilities
- Define CI/CD pipelines and automated build/test workflows.
- Provide containerization via Docker.
- Maintain Kubernetes manifests and deployment descriptors.
- Configure observability (logging, metrics) and monitoring assets.
- Ensure reproducible and consistent development and production environments.

## Boundaries
This module does **not** manage:
- Business rules or domain logic.
- API contracts or application behavior.
- Frontend UI state or logic.

## Security Considerations
As this repository is public, all configuration files must:
- Use placeholders or `.example` variants.
- Reference environment variables symbolically.
- Contain no real secrets, credentials, or identifiers.

Violating these rules is considered a critical defect.
# KML Infrastructure

This directory holds the minimum deployment scaffolding for local, staging, and Kubernetes-style rollout paths.

## Local stack

1. Copy `.env.example` to `.env` and fill secrets.
2. Run `docker compose --env-file infra/.env -f infra/docker-compose.microservices.yml --profile microservices up --build`.
3. Open Prometheus at `http://localhost:9090`, Grafana at `http://localhost:3000`, and Jaeger at `http://localhost:16686`.

## Standalone microservice local stack

KML can run the core backend domains as independently booted Spring services. Each service has its own container, port, service identity, logs, metrics, tracing attributes, and Maven module.

Run the microservice topology from the repository root:

```bash
docker compose --env-file infra/.env -f infra/docker-compose.microservices.yml --profile microservices up --build
```

Default service ports:

- API gateway: `8080`
- User service: `8081`
- Inventory service: `8082`
- Order service: `8083`
- Shipment service: `8084`
- Warehouse service: `8085`
- Admin frontend: `4200`
- Customer frontend: `5173`

Each backend service is built from `services/Dockerfile` with a different Maven module:

- `user-service`
- `inventory-service`
- `order-service`
- `shipment-service`
- `warehouse-service`

The service DNS names are exposed through `KML_SERVICES_*_URL` variables. HTTP clients use these URLs for synchronous calls, while Kafka and RabbitMQ carry asynchronous integration events.

Order creation resolves the customer's current address from user-service through an authenticated internal endpoint. The address is then carried as an immutable order-time snapshot through the reservation event into shipment-service. Set `KML_INTERNAL_SERVICE_TOKEN` from a secret manager in every environment.

OpenTelemetry Collector receives OTLP traces from every Spring service and exports them to Jaeger. Prometheus scrapes each service's Actuator metrics endpoint.

Run the automated cross-service critical path:

```bash
bash scripts/microservice-integration-smoke.sh
```

The services workspace has separate Maven modules, application configs, Docker builds, database URLs, and frontend-facing gateway routes. Domain behavior now belongs in `services/`; new backend work should be added to the matching service module.

For an existing local Postgres volume, the database initialization script will not rerun automatically. If the new service databases are missing, recreate the local Compose volume or create these databases manually:

- `kml_user`
- `kml_order`
- `kml_inventory`
- `kml_warehouse`
- `kml_shipment`

## Kubernetes starter manifests

Apply the manifests in this order:

1. `kml-namespace.yaml`
2. `kml-configmap.yaml`
3. a `Secret` named `kml-secrets`, or the External Secrets template after configuring a real provider
4. `kml-service-discovery.example.yaml` and the service-specific Deployments

`kml-external-secrets.example.yaml` intentionally uses a fake provider. Replace that provider block with AWS Secrets Manager, GCP Secret Manager, Azure Key Vault, or Vault before deployment. Real provider credentials and cluster installation cannot be supplied by this public repository.

## Production notes

- Replace placeholder images with signed, versioned artifacts from CI.
- Put real secrets in a secret manager, not in Git.
- Point Kafka, RabbitMQ, and OpenSearch at managed services or hardened clusters.
- Add ingress, TLS termination, and network policies before public exposure.

## Deployment runbook

### 1. Build verification

Run these from the repo root and send back the output:

- `cd services && ./mvnw -DskipTests package`
- `cd frontend/customer-frontend && npm run build`
- `cd frontend/admin-frontend && npm run build`

If the admin build aborts again, capture the full terminal output exactly as-is.

### 2. Local stack bring-up

- Copy `infra/.env.example` to `infra/.env` and fill the passwords.
- Start the microservice stack: `docker compose --env-file infra/.env -f infra/docker-compose.microservices.yml --profile microservices up --build`
- Check health:
  - API gateway: `curl http://localhost:8080/actuator/health`
  - user service: `curl http://localhost:8081/actuator/health`
  - order service: `curl http://localhost:8083/actuator/health`
  - prometheus: `http://localhost:9090`
  - grafana: `http://localhost:3000`
  - jaeger: `http://localhost:16686`

### 3. Kubernetes rollout

Apply in this order:

1. `kubectl apply -f infra/k8s/kml-namespace.yaml`
2. `kubectl apply -f infra/k8s/kml-configmap.yaml`
3. Configure and apply `infra/k8s/kml-external-secrets.example.yaml`, or apply a locally generated `kml-secrets` Secret
4. Apply `infra/k8s/kml-service-discovery.example.yaml`
5. Apply service-specific API gateway and domain Deployments

### 4. Send back these checks

- `kubectl get pods -n kml`
- `kubectl get svc -n kml`
- `curl http://<api-gateway-service>/actuator/health`
- the first 20 lines of any failing pod logs

### 5. Production hardening still required outside the repo

- Managed secrets
- Ingress + TLS
- Network policies
- Horizontal pod autoscaling
- Backup/restore validation
- Load and soak testing
