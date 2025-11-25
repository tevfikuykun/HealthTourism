@echo off
REM Database Backup Script for Windows
REM This script backs up all PostgreSQL databases

setlocal enabledelayedexpansion

set BACKUP_DIR=.\backups
set TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=!TIMESTAMP: =0!
set PGUSER=%POSTGRES_USER%
if "!PGUSER!"=="" set PGUSER=postgres
set PGPASSWORD=%POSTGRES_PASSWORD%
if "!PGPASSWORD!"=="" set PGPASSWORD=postgres
set PGHOST=%POSTGRES_HOST%
if "!PGHOST!"=="" set PGHOST=localhost
set PGPORT=%POSTGRES_PORT%
if "!PGPORT!"=="" set PGPORT=5432

REM Create backup directory
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo Starting database backup at %date% %time%

REM List of databases to backup
set DATABASES=healthtourism_core healthtourism_auth healthtourism_hospital healthtourism_doctor healthtourism_accommodation healthtourism_flight healthtourism_payment healthtourism_reservation healthtourism_notification

REM Backup each database
for %%D in (%DATABASES%) do (
    echo Backing up database: %%D
    set PGPASSWORD=%PGPASSWORD%
    pg_dump -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %%D -F c -f "%BACKUP_DIR%\%%D_%TIMESTAMP%.dump"
    
    if !errorlevel! equ 0 (
        echo Successfully backed up %%D
    ) else (
        echo Failed to backup %%D
    )
)

echo Backup completed at %date% %time%
echo Backups stored in: %BACKUP_DIR%

pause

