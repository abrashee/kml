-- ===========================================
-- Flyway Migration: Add owner_id to storage_unit_inventory_assignments
-- ===========================================

-- Add owner_id to storage_unit_inventory_item_assignments (nullable initially)
ALTER TABLE storage_unit_inventory_item_assignments
ADD COLUMN owner_id BIGINT REFERENCES users(id);
