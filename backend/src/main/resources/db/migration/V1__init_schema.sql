-- ===========================================
-- Flyway Migration: Initial Schema
-- ===========================================

-- USERS
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- USER ACTIVITY LOG
CREATE TABLE user_activity_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    entity VARCHAR(255) NOT NULL,
    entity_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- WAREHOUSES
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- STORAGE UNITS
CREATE TABLE storage_units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(id),
    capacity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_storage_units UNIQUE (warehouse_id, code)
);

-- INVENTORY ITEMS
CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    owner_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- STORAGE UNIT INVENTORY ASSIGNMENTS
CREATE TABLE storage_unit_inventory_item_assignments (
    id BIGSERIAL PRIMARY KEY,
    storage_unit_id BIGINT NOT NULL REFERENCES storage_units(id),
    inventory_item_id BIGINT NOT NULL REFERENCES inventory_items(id),
    assigned_quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_suia UNIQUE (storage_unit_id, inventory_item_id)
);

-- ORDER STATUSES
CREATE TABLE order_statuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1024),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ORDERS
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    status_id BIGINT NOT NULL REFERENCES order_statuses(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ORDER ITEMS
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    inventory_item_id BIGINT NOT NULL REFERENCES inventory_items(id),
    owner_id BIGINT REFERENCES users(id),  -- added to match entity
    quantity INT NOT NULL,
    price_at_order DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SHIPMENTS
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    tracking VARCHAR(255) NOT NULL UNIQUE,
    carrier_info VARCHAR(255),
    address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SHIPMENT HISTORY
CREATE TABLE shipment_history (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id),
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);