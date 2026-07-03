# Kubernetes manifests

These manifests are reference manifests only.

They are not used by the current OCI Free Tier production deployment.

Current production uses Docker Compose on a single OCI VM.

Do not apply these manifests unless intentionally migrating to Kubernetes.

Important:
- `kml-hpa.yaml` contains HorizontalPodAutoscaler resources.
- HPA can increase pod replicas automatically.
- Kubernetes services of type LoadBalancer, if added in the future, may create paid cloud load balancers.
- Before applying any Kubernetes manifest to OCI, review Free Tier limits and resource quotas.
