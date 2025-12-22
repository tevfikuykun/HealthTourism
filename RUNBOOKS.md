# Runbooks (Operations)

This repo contains a lot of infrastructure and “production-ready” building blocks. This page is the **single operational entry point** for the most common tasks.

## Backups (local/dev)

- **Postgres core (docker-compose)**: `scripts/backup-postgres-core.sh`
- **Notification MySQL (docker-compose)**: `scripts/backup-mysql-notification.sh`
- **Legacy multi-DB MySQL dump script** (requires `mysqldump` installed locally): `scripts/backup-all-databases.sh`

Backups are written to `./backups` by default (override with `BACKUP_DIR`).

## Backups (Kubernetes)

Template CronJobs live at:
- `kubernetes/operations/database-backup-cronjobs.yaml`

You must replace the placeholder secrets (`db-credentials`, `mysql-credentials`) and mount a PVC or push artifacts to object storage.

## Secrets

- For local/dev, use `.env` (see `.env.example`) with `docker-compose.yml`.
- For Kubernetes, store secrets in your secret manager of choice (Vault / AWS Secrets Manager / etc.) and inject as K8s Secrets.

## Monitoring

- Local monitoring stack configs exist under `microservices/monitoring/` and root `monitoring/`.
- Recommended baseline: Prometheus + Grafana + centralized logs (Loki/ELK) + alerting.

## CI/CD

GitHub Actions workflows:
- `/.github/workflows/ci-cd.yml` (build/test/security scan)
- `/.github/workflows/deploy.yml` (build/push + K8s deploy + smoke test)
- `/.github/workflows/frontend-ci.yml` (frontend lint/build/docker)

