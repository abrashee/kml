# Kubernetes Rollback Strategy

## Scope

This rollback strategy applies to KML Kubernetes Deployments:

- api-gateway
- user-service
- inventory-service
- order-service
- warehouse-service
- shipment-service
- admin-frontend
- customer-frontend

## Preconditions

- Images must be tagged with immutable release tags.
- Deployments must not use `latest`.
- Rollouts must use Kubernetes RollingUpdate strategy.
- Health probes must remain enabled.
- Database migrations must be backward-compatible with the previous release.

## Check rollout status

```bash
kubectl -n kml rollout status deployment/api-gateway
kubectl -n kml rollout status deployment/user-service
kubectl -n kml rollout status deployment/inventory-service
kubectl -n kml rollout status deployment/order-service
kubectl -n kml rollout status deployment/warehouse-service
kubectl -n kml rollout status deployment/shipment-service
kubectl -n kml rollout status deployment/admin-frontend
kubectl -n kml rollout status deployment/customer-frontend
```

## View rollout history

```bash
kubectl -n kml rollout history deployment/api-gateway
kubectl -n kml rollout history deployment/user-service
kubectl -n kml rollout history deployment/inventory-service
kubectl -n kml rollout history deployment/order-service
kubectl -n kml rollout history deployment/warehouse-service
kubectl -n kml rollout history deployment/shipment-service
kubectl -n kml rollout history deployment/admin-frontend
kubectl -n kml rollout history deployment/customer-frontend
```

## Roll back one deployment

```bash
kubectl -n kml rollout undo deployment/<deployment-name>
kubectl -n kml rollout status deployment/<deployment-name>
```

## Roll back to a specific revision

```bash
kubectl -n kml rollout undo deployment/<deployment-name> --to-revision=<revision>
kubectl -n kml rollout status deployment/<deployment-name>
```

## Emergency full application rollback

Rollback order:

1. customer-frontend
2. admin-frontend
3. api-gateway
4. shipment-service
5. warehouse-service
6. inventory-service
7. order-service
8. user-service

```bash
for deployment in \
  customer-frontend \
  admin-frontend \
  api-gateway \
  shipment-service \
  warehouse-service \
  inventory-service \
  order-service \
  user-service
do
  kubectl -n kml rollout undo deployment/$deployment
  kubectl -n kml rollout status deployment/$deployment
done
```

## Post-rollback validation

```bash
kubectl -n kml get pods
kubectl -n kml get deploy
kubectl -n kml get ingress
kubectl -n kml get hpa
```

Then run:

- Health checks
- Smoke tests
- Critical login flow
- Product listing flow
- Order creation flow
- Inventory reservation flow
- Shipment creation flow

## Non-rollback-safe changes

Do not rely on Kubernetes rollback alone for:

- Destructive database migrations
- Irreversible data transformations
- Message schema changes without backward compatibility
- Security secret rotation without retaining old secret compatibility
