-- Flight Service Database Schema
CREATE DATABASE IF NOT EXISTS flight_db;
USE flight_db;

CREATE TABLE IF NOT EXISTS flight_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airline_name VARCHAR(255) NOT NULL,
    flight_number VARCHAR(50) NOT NULL,
    departure_city VARCHAR(100) NOT NULL,
    arrival_city VARCHAR(100) NOT NULL,
    departure_date_time DATETIME NOT NULL,
    arrival_date_time DATETIME NOT NULL,
    flight_class VARCHAR(50) NOT NULL,
    available_seats INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    has_meal BOOLEAN NOT NULL,
    has_entertainment BOOLEAN NOT NULL,
    baggage_allowance INT NOT NULL,
    is_direct_flight BOOLEAN NOT NULL,
    description TEXT,
    rating DOUBLE NOT NULL,
    total_reviews INT NOT NULL,
    is_available BOOLEAN NOT NULL,
    INDEX idx_departure (departure_city, departure_date_time),
    INDEX idx_arrival (arrival_city),
    INDEX idx_available (is_available),
    INDEX idx_date (departure_date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


