-- FAQ Service Database Schema
CREATE DATABASE IF NOT EXISTS faq_db;
USE faq_db;

CREATE TABLE IF NOT EXISTS faqs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    answer VARCHAR(2000) NOT NULL,
    category VARCHAR(100) NOT NULL,
    display_order INT NOT NULL,
    is_active BOOLEAN NOT NULL,
    INDEX idx_category (category),
    INDEX idx_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


