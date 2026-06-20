ALTER TABLE orders ADD COLUMN shipping_address VARCHAR(500);

UPDATE orders
SET shipping_address = 'LEGACY_ORDER_ADDRESS_REQUIRES_REVIEW'
WHERE shipping_address IS NULL;

ALTER TABLE orders ALTER COLUMN shipping_address SET NOT NULL;
