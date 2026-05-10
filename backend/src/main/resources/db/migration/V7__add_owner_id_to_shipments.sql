-- ===========================================
-- Flyway Migration: Add owner_id to shipments
-- ===========================================

-- Add owner_id to shipments table (nullable initially)
ALTER TABLE shipments
ADD COLUMN owner_id BIGINT REFERENCES users(id);
