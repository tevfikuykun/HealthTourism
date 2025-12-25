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
    INDEX idx_package_name (package_name),
    INDEX idx_package_type (package_type),
    INDEX idx_hospital (hospital_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_accommodation (accommodation_id),
    INDEX idx_is_active (is_active),
    INDEX idx_rating (rating),
    INDEX idx_final_price (final_price),
    INDEX idx_includes_flight (includes_flight),
    -- Composite indexes for common query patterns
    INDEX idx_type_active (package_type, is_active),
    INDEX idx_hospital_active (hospital_id, is_active),
    INDEX idx_type_flight_active (package_type, includes_flight, is_active),
    INDEX idx_price_active (final_price, is_active),
    INDEX idx_rating_active (rating, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


