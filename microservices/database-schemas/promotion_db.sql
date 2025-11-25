CREATE DATABASE IF NOT EXISTS promotion_db;
USE promotion_db;

CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(19,2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    max_uses INT NOT NULL,
    used_count INT NOT NULL,
    min_purchase_amount DECIMAL(19,2) NOT NULL,
    max_discount_amount DECIMAL(19,2) NOT NULL,
    is_active BOOLEAN NOT NULL,
    applicable_service_type VARCHAR(100) NOT NULL,
    specific_service_id BIGINT,
    created_at DATETIME NOT NULL
);

CREATE INDEX idx_code ON promotions(code);
CREATE INDEX idx_active_dates ON promotions(is_active, start_date, end_date);

