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
    INDEX idx_company_name (company_name),
    INDEX idx_vehicle_type (vehicle_type),
    INDEX idx_service_type (service_type),
    INDEX idx_pickup_location (pickup_location),
    INDEX idx_dropoff_location (dropoff_location),
    INDEX idx_available (is_available),
    INDEX idx_rating (rating),
    INDEX idx_price (price),
    INDEX idx_meet_greet (has_meet_and_greet),
    -- Composite indexes for common query patterns
    INDEX idx_pickup_dropoff_available (pickup_location, dropoff_location, is_available),
    INDEX idx_type_capacity_available (vehicle_type, passenger_capacity, is_available),
    INDEX idx_price_available (price, is_available),
    INDEX idx_rating_available (rating, is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Transfer Reservations Table
-- Sağlık turizminde transfer rezervasyonları - havalimanı karşılaması için kritik
CREATE TABLE IF NOT EXISTS transfer_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    transfer_service_id BIGINT NOT NULL,
    transfer_date_time DATETIME NOT NULL,
    number_of_passengers INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, CONFIRMED, CANCELLED, COMPLETED
    special_requests VARCHAR(500),
    flight_number VARCHAR(50), -- Uçuş takip servisleri için kritik
    created_at DATETIME NOT NULL,
    INDEX idx_reservation_number (reservation_number),
    INDEX idx_user (user_id),
    INDEX idx_transfer_service (transfer_service_id),
    INDEX idx_status (status),
    INDEX idx_transfer_date_time (transfer_date_time),
    INDEX idx_created_at (created_at),
    INDEX idx_flight_number (flight_number),
    -- Composite indexes for common query patterns
    INDEX idx_status_date (status, transfer_date_time),
    INDEX idx_user_status (user_id, status),
    INDEX idx_service_date_status (transfer_service_id, transfer_date_time, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


