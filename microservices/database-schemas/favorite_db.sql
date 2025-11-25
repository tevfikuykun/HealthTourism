-- Favorite Service Database Schema
CREATE DATABASE IF NOT EXISTS favorite_db;
USE favorite_db;

CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_user_item (user_id, item_type, item_id),
    INDEX idx_user (user_id),
    INDEX idx_item (item_type, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


