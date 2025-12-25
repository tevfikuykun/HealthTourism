#!/bin/bash
set -euo pipefail

# Backup Postgres used by core services (docker-compose friendly)
#
# Usage:
#   POSTGRES_CORE_USER=postgres POSTGRES_CORE_PASSWORD=... POSTGRES_CORE_DB=healthtourism_core ./scripts/backup-postgres-core.sh

BACKUP_DIR="${BACKUP_DIR:-./backups}"
DATE="$(date +%Y%m%d_%H%M%S)"
CONTAINER="${POSTGRES_CONTAINER:-healthtourism-postgres-core}"

DB="${POSTGRES_CORE_DB:-healthtourism_core}"
USER="${POSTGRES_CORE_USER:-postgres}"
PASSWORD="${POSTGRES_CORE_PASSWORD:-postgres}"

mkdir -p "$BACKUP_DIR"

OUT="$BACKUP_DIR/${DB}_${DATE}.dump"
echo "Backing up Postgres database '$DB' from container '$CONTAINER' to $OUT"

docker exec -e PGPASSWORD="$PASSWORD" "$CONTAINER" \
  pg_dump -U "$USER" -d "$DB" -Fc > "$OUT"

gzip -f "$OUT"
echo "Done: ${OUT}.gz"

