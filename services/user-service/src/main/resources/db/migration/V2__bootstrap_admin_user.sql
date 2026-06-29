INSERT INTO users (
    name,
    username,
    password,
    role,
    account_status,
    created_at,
    updated_at
)
SELECT
    'Bootstrap Admin',
    'admin',
    '$2b$10$0uLIatOCOffdwUOzhXdsHu9TQ7hVdBa6PZlmZzmFvkqBwc1/b0wIO',
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);
