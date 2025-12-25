CREATE DATABASE IF NOT EXISTS visa_consultation_db;
USE visa_consultation_db;

CREATE TABLE IF NOT EXISTS visa_consultancies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    visa_type VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    service_fee DECIMAL(19,2) NOT NULL,
    consultancy_fee DECIMAL(19,2),
    processing_days INT NOT NULL,
    average_processing_days INT,
    success_rate DOUBLE NOT NULL DEFAULT 0.0,
    supported_countries VARCHAR(500),
    is_emergency_service_available BOOLEAN NOT NULL DEFAULT FALSE,
    includes_translation BOOLEAN NOT NULL,
    includes_document_preparation BOOLEAN NOT NULL,
    includes_appointment_booking BOOLEAN NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL,
    INDEX idx_company_name (company_name),
    INDEX idx_visa_type (visa_type),
    INDEX idx_country (country),
    INDEX idx_is_available (is_available),
    INDEX idx_rating (rating),
    INDEX idx_success_rate (success_rate),
    INDEX idx_processing_days (processing_days),
    INDEX idx_emergency (is_emergency_service_available),
    INDEX idx_consultancy_fee (consultancy_fee),
    -- Composite indexes for common query patterns
    INDEX idx_country_type_available (country, visa_type, is_available),
    INDEX idx_type_success_available (visa_type, success_rate, is_available),
    INDEX idx_emergency_available (is_emergency_service_available, is_available),
    INDEX idx_processing_available (processing_days, is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

