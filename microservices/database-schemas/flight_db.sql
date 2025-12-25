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
    total_seats INT NOT NULL,
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
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_departure (departure_city, departure_date_time),
    INDEX idx_arrival (arrival_city),
    INDEX idx_available (is_available),
    INDEX idx_date (departure_date_time),
    -- Composite indexes for common query patterns
    INDEX idx_route (departure_city, arrival_city),
    INDEX idx_airline (airline_name),
    INDEX idx_available_seats (available_seats, is_available),
    INDEX idx_date_range (departure_date_time, arrival_date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Flight Reservations Table
CREATE TABLE IF NOT EXISTS flight_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(100) NOT NULL UNIQUE,
    pnr_code VARCHAR(50) UNIQUE,
    user_id BIGINT NOT NULL,
    flight_booking_id BIGINT NOT NULL,
    number_of_passengers INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_reservation_number (reservation_number),
    INDEX idx_pnr_code (pnr_code),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_flight_booking (flight_booking_id),
    -- Composite indexes for common query patterns
    INDEX idx_user_status (user_id, status),
    INDEX idx_status_created (status, created_at),
    INDEX idx_created_at_status (created_at, status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (flight_booking_id) REFERENCES flight_bookings(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


