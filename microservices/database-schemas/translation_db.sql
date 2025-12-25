CREATE DATABASE IF NOT EXISTS translation_db;
USE translation_db;

CREATE TABLE IF NOT EXISTS translation_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    translator_name VARCHAR(255) NOT NULL,
    languages VARCHAR(500) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    is_certified BOOLEAN NOT NULL,
    is_available_for_hospital BOOLEAN NOT NULL,
    is_available_for_consultation BOOLEAN NOT NULL,
    price_per_hour DECIMAL(19,2) NOT NULL,
    price_per_document DECIMAL(19,2) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL,
    INDEX idx_translator_name (translator_name),
    INDEX idx_service_type (service_type),
    INDEX idx_is_certified (is_certified),
    INDEX idx_is_available (is_available),
    INDEX idx_rating (rating),
    INDEX idx_price_per_hour (price_per_hour),
    INDEX idx_hospital_available (is_available_for_hospital),
    INDEX idx_consultation_available (is_available_for_consultation),
    -- Composite indexes for common query patterns
    INDEX idx_certified_available (is_certified, is_available),
    INDEX idx_type_certified_available (service_type, is_certified, is_available),
    INDEX idx_rating_available (rating, is_available),
    INDEX idx_hospital_certified_available (is_available_for_hospital, is_certified, is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

