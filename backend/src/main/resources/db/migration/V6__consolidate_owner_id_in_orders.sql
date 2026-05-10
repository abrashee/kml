-- ===========================================
-- Flyway Migration: Consolidate owner_id in orders table
-- ===========================================

-- Copydata from user_id to owner_id where owner_id is null
UPDATE orders SET owner_id = user_id WHERE owner_id IS NULL;

-- Drop the foreign key constraint on user_id and the column itself
ALTER TABLE orders DROP COLUMN user_id;
