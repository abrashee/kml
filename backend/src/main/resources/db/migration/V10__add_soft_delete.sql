ALTER TABLE inventory_items ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE shipments ADD COLUMN deleted_at TIMESTAMP;
