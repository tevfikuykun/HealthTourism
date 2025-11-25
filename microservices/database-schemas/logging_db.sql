-- Logging Service Database Schema
CREATE DATABASE IF NOT EXISTS logging_db;
USE logging_db;

CREATE TABLE IF NOT EXISTS log_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    message TEXT,
    exception TEXT,
    user_id VARCHAR(255),
    request_id VARCHAR(255),
    timestamp DATETIME NOT NULL,
    INDEX idx_service_level (service_name, level),
    INDEX idx_timestamp (timestamp),
    INDEX idx_level (level),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


