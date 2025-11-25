CREATE DATABASE IF NOT EXISTS visa_consultation_db;
USE visa_consultation_db;

CREATE TABLE IF NOT EXISTS visa_consultancies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    visa_type VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    service_fee DECIMAL(19,2) NOT NULL,
    processing_days INT NOT NULL,
    includes_translation BOOLEAN NOT NULL,
    includes_document_preparation BOOLEAN NOT NULL,
    includes_appointment_booking BOOLEAN NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL
);

