#!/bin/bash

# Backup script for all databases
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Database credentials
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"

# List of all databases
DATABASES=(
    "auth_db:3329"
    "user_db:3307"
    "hospital_db:3308"
    "doctor_db:3309"
    "accommodation_db:3310"
    "flight_db:3311"
    "car_rental_db:3312"
    "transfer_db:3313"
    "package_db:3314"
    "reservation_db:3315"
    "payment_db:3316"
    "notification_db:3317"
    "medical_document_db:3318"
    "telemedicine_db:3319"
    "patient_followup_db:3320"
    "blog_db:3321"
    "faq_db:3322"
    "favorite_db:3323"
    "appointment_calendar_db:3324"
    "contact_db:3325"
    "testimonial_db:3326"
    "gallery_db:3327"
    "insurance_db:3328"
)

echo "Starting database backup at $(date)"

for db_info in "${DATABASES[@]}"; do
    IFS=':' read -r db_name db_port <<< "$db_info"
    
    BACKUP_FILE="$BACKUP_DIR/${db_name}_${DATE}.sql"
    
    echo "Backing up $db_name..."
    
    mysqldump -h localhost -P "$db_port" -u "$DB_USER" -p"$DB_PASSWORD" \
        --single-transaction \
        --routines \
        --triggers \
        "$db_name" > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✓ Successfully backed up $db_name"
        # Compress backup
        gzip "$BACKUP_FILE"
    else
        echo "✗ Failed to backup $db_name"
    fi
done

# Cleanup old backups
echo "Cleaning up backups older than $RETENTION_DAYS days..."
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completed at $(date)"

