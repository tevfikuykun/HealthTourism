-- Review Service Database Schema
CREATE DATABASE IF NOT EXISTS review_db;
USE review_db;

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    doctor_id BIGINT,
    hospital_id BIGINT,
    review_type VARCHAR(50) NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(2000),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50),
    created_at DATETIME NOT NULL,
    INDEX idx_doctor (doctor_id),
    INDEX idx_hospital (hospital_id),
    INDEX idx_user (user_id),
    INDEX idx_approved (is_approved),
    INDEX idx_created_at (created_at),
    INDEX idx_rating (rating),
    -- Composite indexes for common query patterns
    INDEX idx_doctor_approved (doctor_id, is_approved),
    INDEX idx_hospital_approved (hospital_id, is_approved),
    INDEX idx_review_type_approved (review_type, is_approved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

