-- Telemedicine Service Database Schema
CREATE DATABASE IF NOT EXISTS telemedicine_db;
USE telemedicine_db;

CREATE TABLE IF NOT EXISTS telemedicine_consultations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    consultation_date DATETIME NOT NULL,
    duration_minutes INT NOT NULL,
    consultation_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    meeting_link VARCHAR(1000),
    created_at DATETIME NOT NULL,
    INDEX idx_user (user_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_status (status),
    INDEX idx_consultation_date (consultation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


