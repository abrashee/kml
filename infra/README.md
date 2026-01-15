# Infrastructure

## Purpose
The infra module contains all infrastructure and operational assets required
to build, test, and deploy the KML system.

This includes CI/CD configuration, containerization, deployment manifests,
and environment configuration templates.

## Responsibilities
- CI/CD pipeline definitions
- Build and test automation
- Docker and container orchestration
- Kubernetes manifests and deployment descriptors
- Observability configuration (logging, metrics)

## What This Module Does NOT Own
- Business logic
- API contracts
- Frontend behavior or state

## Security Notice
This repository is public.

All configuration files must:
- Use placeholders or `.example` variants
- Reference environment variables symbolically
- Contain no real secrets, credentials, or identifiers

Violations of this rule are considered critical defects.
