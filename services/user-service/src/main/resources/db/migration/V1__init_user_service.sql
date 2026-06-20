CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    account_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    address VARCHAR(500),
    avatar_url VARCHAR(2048),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE managers (
    user_id BIGINT PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    CONSTRAINT fk_managers_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE workers (
    user_id BIGINT PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    manager_id BIGINT,
    CONSTRAINT fk_workers_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE user_activity_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    entity VARCHAR(255),
    owner_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    entity_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_account_status ON users(account_status);
CREATE INDEX idx_managers_warehouse ON managers(warehouse_id);
CREATE INDEX idx_workers_warehouse ON workers(warehouse_id);
CREATE INDEX idx_workers_manager ON workers(manager_id);
CREATE INDEX idx_activity_log_owner_id ON user_activity_log(owner_id);
CREATE INDEX idx_activity_log_user_id ON user_activity_log(user_id);
CREATE INDEX idx_activity_log_entity_id ON user_activity_log(entity_id);
