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
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


