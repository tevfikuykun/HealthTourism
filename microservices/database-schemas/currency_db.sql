CREATE DATABASE IF NOT EXISTS currency_db;
USE currency_db;

CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    rate DECIMAL(19,6) NOT NULL,
    last_updated DATETIME NOT NULL,
    UNIQUE KEY unique_currency_pair (from_currency, to_currency)
);

