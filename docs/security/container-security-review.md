# Container Security Review

## Status

Container security review is completed for the current KML demo infrastructure scope.

## Completed controls

- Official/vendor infrastructure images are used.
- Infrastructure images were refreshed to newer supported versions where compatible.
- Trivy scans were executed for runtime infrastructure images.
- Docker published ports are bound to 127.0.0.1 instead of 0.0.0.0.
- A Docker Compose hardening override was added.
- `no-new-privileges:true` is applied to infrastructure containers.
- Read-only filesystem is applied to exporter and OpenTelemetry collector containers.
- Temporary writable paths are provided with tmpfs where needed.
- Existing microservice smoke test passes after hardening.

## Residual risk

Some third-party images still report HIGH or CRITICAL findings in Trivy. These findings are in upstream vendor images or bundled binaries.

Current residual-risk handling:

- Keep official images.
- Keep images upgraded to the newest tested compatible versions.
- Do not expose infrastructure ports publicly.
- Track upstream patched releases.
- Re-scan before production deployment.
- Rebuild or replace only images with unacceptable exploitable risk.

## Current decision

This is acceptable for a production-style demo environment, but not certified as vulnerability-free.

Before real production deployment, remaining HIGH/CRITICAL findings must be reviewed against exploitability, exposure, and upstream patch availability.
