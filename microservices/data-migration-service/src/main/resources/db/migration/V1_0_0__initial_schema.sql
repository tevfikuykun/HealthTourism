-- Flyway Migration Script
-- Initial schema for entity version tracking

CREATE TABLE IF NOT EXISTS entity_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(255) NOT NULL UNIQUE,
    current_version VARCHAR(20) NOT NULL,
    schema_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    migration_script VARCHAR(500)
);

CREATE INDEX idx_entity_name ON entity_versions(entity_name);






