-- Hospital Service Database Schema
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE IF NOT EXISTS hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    airport_distance DOUBLE NOT NULL,
    airport_distance_minutes INT NOT NULL,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_active BOOLEAN NOT NULL,
    INDEX idx_city (city),
    INDEX idx_district (district),
    INDEX idx_active (is_active),
    INDEX idx_rating (rating),
    INDEX idx_airport_distance (airport_distance),
    -- Composite indexes for common query patterns
    INDEX idx_city_active (city, is_active),
    INDEX idx_city_district_active (city, district, is_active),
    INDEX idx_rating_airport (rating, airport_distance)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hospital Accreditations Table (ElementCollection)
-- Sağlık turizminde güven her şeydir - JCI, ISO gibi akreditasyonlar kurumsal güvenilirliği artırır
CREATE TABLE IF NOT EXISTS hospital_accreditations (
    hospital_id BIGINT NOT NULL,
    accreditation VARCHAR(100) NOT NULL,
    PRIMARY KEY (hospital_id, accreditation),
    INDEX idx_hospital_accreditation (accreditation),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

