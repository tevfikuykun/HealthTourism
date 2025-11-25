-- Monitoring Service Database Schema
CREATE DATABASE IF NOT EXISTS monitoring_db;
USE monitoring_db;

CREATE TABLE IF NOT EXISTS service_health (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    response_time BIGINT,
    checked_at DATETIME NOT NULL,
    INDEX idx_service (service_name),
    INDEX idx_status (status),
    INDEX idx_checked_at (checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(50),
    recorded_at DATETIME NOT NULL,
    INDEX idx_service_metric (service_name, metric_name),
    INDEX idx_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


