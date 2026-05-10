-- ===========================================
-- Flyway Migration: Add additional owner_id to orders
-- ===========================================

-- Note: order_items already has owner_id from V1
-- Only add owner_id to orders table which is missing it

ALTER TABLE orders
ADD COLUMN owner_id BIGINT REFERENCES users(id);
