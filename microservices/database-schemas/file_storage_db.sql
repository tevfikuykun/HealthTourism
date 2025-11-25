-- File Storage Service Database Schema
CREATE DATABASE IF NOT EXISTS file_storage_db;
USE file_storage_db;

CREATE TABLE IF NOT EXISTS file_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_by VARCHAR(255),
    service_name VARCHAR(255),
    category VARCHAR(100),
    uploaded_at DATETIME NOT NULL,
    INDEX idx_service (service_name),
    INDEX idx_category (category),
    INDEX idx_uploaded_by (uploaded_by),
    INDEX idx_uploaded_at (uploaded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


