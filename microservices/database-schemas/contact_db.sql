-- Contact Service Database Schema
CREATE DATABASE IF NOT EXISTS contact_db;
USE contact_db;

CREATE TABLE IF NOT EXISTS contact_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    responded_at DATETIME,
    response VARCHAR(2000),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


