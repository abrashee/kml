# Kubernetes Restore Testing

## Objective

Verify that backups can be restored successfully.

## PostgreSQL

- Restore the latest backup into a clean database.
- Verify all schemas.
- Verify Flyway history.
- Verify application startup.
- Verify sample business data.

## OpenSearch

- Restore the latest snapshot.
- Verify indices.
- Verify document counts.
- Verify search functionality.

## Kubernetes

Restore:

- ConfigMaps
- Secrets
- Deployments
- Services
- Ingress
- HPA
- NetworkPolicies
- PodDisruptionBudgets

## Validation

Verify:

- Pods become Ready.
- Health endpoints return UP.
- Gateway routing works.
- User login works.
- Product listing works.
- Order creation works.
- Shipment creation works.

## Restore Frequency

Perform a full restore verification at least once every quarter.

## Success Criteria

- No data corruption.
- No missing resources.
- All services operational.
- Business workflows execute successfully.
