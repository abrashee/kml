CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT NOT NULL,
    sku VARCHAR(80) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    storage_unit_id BIGINT NOT NULL,
    reorder_threshold INT NOT NULL DEFAULT 0,
    safety_stock_level INT NOT NULL DEFAULT 0,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_inventory_sku_storage UNIQUE (sku, storage_unit_id)
);

CREATE INDEX idx_inventory_sku ON inventory_items(sku);
CREATE INDEX idx_inventory_warehouse ON inventory_items(warehouse_id);
CREATE INDEX idx_inventory_storage_unit ON inventory_items(storage_unit_id);
