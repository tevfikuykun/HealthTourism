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
    is_available BOOLEAN NOT NULL
);

