-- ===========================================
-- Flyway Migration: Align shipment_history schema with AuditableEntity
-- ===========================================

-- Add missing columns to match AuditableEntity
ALTER TABLE shipment_history
ADD COLUMN owner_id BIGINT REFERENCES users(id);

-- Rename changed_at to created_at (using changed_at as the creation timestamp)
ALTER TABLE shipment_history
RENAME COLUMN changed_at TO created_at;

-- Add updated_at column
ALTER TABLE shipment_history
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
