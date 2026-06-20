CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE storage_units (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    code VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    remaining_capacity INT NOT NULL,
    CONSTRAINT uq_storage_unit_code_per_warehouse UNIQUE (warehouse_id, code)
);

CREATE INDEX idx_warehouses_owner_user_id ON warehouses(owner_user_id);
CREATE INDEX idx_storage_units_warehouse_id ON storage_units(warehouse_id);
