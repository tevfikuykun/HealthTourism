-- Patient Follow-up Service Database Schema
CREATE DATABASE IF NOT EXISTS patient_followup_db;
USE patient_followup_db;

CREATE TABLE IF NOT EXISTS patient_followups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    follow_up_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    recovery_progress TEXT,
    created_at DATETIME NOT NULL,
    completed_at DATETIME,
    INDEX idx_user (user_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_reservation (reservation_id),
    INDEX idx_status (status),
    INDEX idx_follow_up_date (follow_up_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


