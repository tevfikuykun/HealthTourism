-- Package Service Database Schema
CREATE DATABASE IF NOT EXISTS package_db;
USE package_db;

CREATE TABLE IF NOT EXISTS travel_packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(255) NOT NULL,
    package_type VARCHAR(100) NOT NULL,
    duration_days INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    final_price DECIMAL(10, 2) NOT NULL,
    includes_flight BOOLEAN NOT NULL,
    includes_accommodation BOOLEAN NOT NULL,
    includes_transfer BOOLEAN NOT NULL,
    includes_car_rental BOOLEAN NOT NULL,
    includes_visa_service BOOLEAN NOT NULL,
    includes_translation BOOLEAN NOT NULL,
    includes_insurance BOOLEAN NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_active BOOLEAN NOT NULL,
    hospital_id BIGINT NOT NULL,
    doctor_id BIGINT,
    accommodation_id BIGINT,
    INDEX idx_hospital (hospital_id),
    INDEX idx_package_type (package_type),
    INDEX idx_active (is_active),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


