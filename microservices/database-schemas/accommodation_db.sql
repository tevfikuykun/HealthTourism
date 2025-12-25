-- Accommodation Service Database Schema
CREATE DATABASE IF NOT EXISTS accommodation_db;
USE accommodation_db;

CREATE TABLE IF NOT EXISTS accommodations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    distance_to_hospital DOUBLE NOT NULL,
    distance_to_hospital_minutes INT NOT NULL,
    airport_distance DOUBLE NOT NULL,
    airport_distance_minutes INT NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    star_rating INT NOT NULL,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    has_wifi BOOLEAN NOT NULL,
    has_parking BOOLEAN NOT NULL,
    has_breakfast BOOLEAN NOT NULL,
    is_active BOOLEAN NOT NULL,
    hospital_id BIGINT NOT NULL,
    INDEX idx_hospital (hospital_id),
    INDEX idx_city (city),
    INDEX idx_rating (rating),
    -- Composite index for health tourism queries: finding hotels near hospitals
    -- This index optimizes queries that filter by hospital_id and distance_to_hospital
    INDEX idx_hospital_distance (hospital_id, distance_to_hospital),
    -- Index for price range searches
    INDEX idx_price_active (price_per_night, is_active),
    -- Index for city and active status (common filter combination)
    INDEX idx_city_active (city, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


