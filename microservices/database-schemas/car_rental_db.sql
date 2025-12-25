-- Car Rental Service Database Schema
CREATE DATABASE IF NOT EXISTS car_rental_db;
USE car_rental_db;

CREATE TABLE IF NOT EXISTS car_rentals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    car_model VARCHAR(255) NOT NULL,
    car_type VARCHAR(100) NOT NULL,
    passenger_capacity INT NOT NULL,
    luggage_capacity INT NOT NULL,
    transmission VARCHAR(50) NOT NULL,
    has_air_conditioning BOOLEAN NOT NULL,
    has_gps BOOLEAN NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    price_per_week DECIMAL(10, 2) NOT NULL,
    price_per_month DECIMAL(10, 2) NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    dropoff_location VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL,
    includes_insurance BOOLEAN NOT NULL,
    includes_driver BOOLEAN NOT NULL,
    INDEX idx_car_type (car_type),
    INDEX idx_available (is_available),
    INDEX idx_rating (rating),
    -- Composite index for capacity-based searches (critical for health tourism)
    INDEX idx_type_capacity (car_type, passenger_capacity),
    -- Index for price range searches
    INDEX idx_price_available (price_per_day, is_available),
    -- Index for location-based searches
    INDEX idx_pickup_location (pickup_location(100)),
    -- Index for driver and availability filtering
    INDEX idx_driver_available (includes_driver, is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Car Rental Reservations Table (for date conflict checking)
CREATE TABLE IF NOT EXISTS car_rental_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(100) NOT NULL UNIQUE,
    car_rental_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    pickup_date DATE NOT NULL,
    dropoff_date DATE NOT NULL,
    rental_days INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_car_rental (car_rental_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    -- Composite index for date conflict checking (critical for preventing double booking)
    INDEX idx_car_dates_status (car_rental_id, pickup_date, dropoff_date, status),
    -- Index for date range queries
    INDEX idx_dates (pickup_date, dropoff_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


