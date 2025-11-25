-- Transfer Service Database Schema
CREATE DATABASE IF NOT EXISTS transfer_db;
USE transfer_db;

CREATE TABLE IF NOT EXISTS transfer_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    vehicle_type VARCHAR(100) NOT NULL,
    passenger_capacity INT NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    dropoff_location VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    distance_km DOUBLE NOT NULL,
    has_meet_and_greet BOOLEAN NOT NULL,
    has_luggage_assistance BOOLEAN NOT NULL,
    has_wifi BOOLEAN NOT NULL,
    has_child_seat BOOLEAN NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL,
    INDEX idx_service_type (service_type),
    INDEX idx_available (is_available),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


