-- ===========================================
-- Flyway Migration: Add owner_id to order_statuses, warehouses, and storage_units
-- ===========================================

-- Add owner_id to order_statuses (nullable initially to avoid FK constraint violations)
ALTER TABLE order_statuses
ADD COLUMN owner_id BIGINT REFERENCES users(id);

-- Add owner_id to warehouses (nullable initially)
ALTER TABLE warehouses
ADD COLUMN owner_id BIGINT REFERENCES users(id);

-- Add owner_id to storage_units (nullable initially)
ALTER TABLE storage_units
ADD COLUMN owner_id BIGINT REFERENCES users(id);


