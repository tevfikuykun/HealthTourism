-- Insurance Service Database Schema
CREATE DATABASE IF NOT EXISTS insurance_db;
USE insurance_db;

CREATE TABLE IF NOT EXISTS insurances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(100) NOT NULL,
    coverage_area VARCHAR(100) NOT NULL,
    coverage_amount DECIMAL(15, 2) NOT NULL,
    premium DECIMAL(10, 2) NOT NULL,
    validity_days INT NOT NULL,
    coverage_details TEXT,
    exclusions TEXT,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_active BOOLEAN NOT NULL,
    INDEX idx_insurance_type (insurance_type),
    INDEX idx_active (is_active),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


