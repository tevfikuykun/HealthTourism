#!/bin/bash

# Database Backup Script
# This script backs up all PostgreSQL databases

BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
PGUSER=${POSTGRES_USER:-postgres}
PGPASSWORD=${POSTGRES_PASSWORD:-postgres}
PGHOST=${POSTGRES_HOST:-localhost}
PGPORT=${POSTGRES_PORT:-5432}

# Create backup directory
mkdir -p "$BACKUP_DIR"

echo "Starting database backup at $(date)"

# List of databases to backup
DATABASES=(
    "healthtourism_core"
    "healthtourism_auth"
    "healthtourism_hospital"
    "healthtourism_doctor"
    "healthtourism_accommodation"
    "healthtourism_flight"
    "healthtourism_payment"
    "healthtourism_reservation"
    "healthtourism_notification"
    "healthtourism_car_rental"
    "healthtourism_transfer"
    "healthtourism_package"
    "healthtourism_medical_document"
    "healthtourism_telemedicine"
    "healthtourism_patient_followup"
    "healthtourism_blog"
    "healthtourism_faq"
    "healthtourism_favorite"
    "healthtourism_appointment_calendar"
    "healthtourism_contact"
    "healthtourism_testimonial"
    "healthtourism_gallery"
    "healthtourism_insurance"
    "healthtourism_visa_consultation"
    "healthtourism_translation"
    "healthtourism_currency"
    "healthtourism_chat"
    "healthtourism_promotion"
)

# Backup each database
for DB in "${DATABASES[@]}"; do
    echo "Backing up database: $DB"
    PGPASSWORD=$PGPASSWORD pg_dump -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$DB" -F c -f "$BACKUP_DIR/${DB}_${TIMESTAMP}.dump"
    
    if [ $? -eq 0 ]; then
        echo "✓ Successfully backed up $DB"
    else
        echo "✗ Failed to backup $DB"
    fi
done

echo "Backup completed at $(date)"
echo "Backups stored in: $BACKUP_DIR"

# Cleanup old backups (keep last 7 days)
find "$BACKUP_DIR" -name "*.dump" -mtime +7 -delete
echo "Old backups cleaned up"

