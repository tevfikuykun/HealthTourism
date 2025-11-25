-- Appointment Calendar Service Database Schema
CREATE DATABASE IF NOT EXISTS appointment_calendar_db;
USE appointment_calendar_db;

CREATE TABLE IF NOT EXISTS appointment_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_available BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_doctor (doctor_id),
    INDEX idx_available (is_available),
    INDEX idx_start_time (start_time),
    INDEX idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


