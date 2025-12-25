#!/bin/bash
set -euo pipefail

# Backup MySQL notification DB (docker-compose friendly)

BACKUP_DIR="${BACKUP_DIR:-./backups}"
DATE="$(date +%Y%m%d_%H%M%S)"
CONTAINER="${MYSQL_CONTAINER:-healthtourism-mysql-notification}"

DB="${MYSQL_NOTIFICATION_DB:-notification_db}"
ROOT_PASSWORD="${MYSQL_NOTIFICATION_ROOT_PASSWORD:-root}"

mkdir -p "$BACKUP_DIR"

OUT="$BACKUP_DIR/${DB}_${DATE}.sql"
echo "Backing up MySQL database '$DB' from container '$CONTAINER' to $OUT"

docker exec "$CONTAINER" \
  mysqldump -u root "-p${ROOT_PASSWORD}" --single-transaction --routines --triggers "$DB" > "$OUT"

gzip -f "$OUT"
echo "Done: ${OUT}.gz"

