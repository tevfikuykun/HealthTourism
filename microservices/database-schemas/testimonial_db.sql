-- Testimonial Service Database Schema
CREATE DATABASE IF NOT EXISTS testimonial_db;
USE testimonial_db;

CREATE TABLE IF NOT EXISTS testimonials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    patient_name VARCHAR(255) NOT NULL,
    country VARCHAR(100) NOT NULL,
    hospital_id BIGINT NOT NULL,
    doctor_id BIGINT,
    testimonial VARCHAR(2000) NOT NULL,
    rating INT NOT NULL,
    video_url VARCHAR(1000),
    image_url VARCHAR(1000),
    is_approved BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_hospital (hospital_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_approved (is_approved),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


