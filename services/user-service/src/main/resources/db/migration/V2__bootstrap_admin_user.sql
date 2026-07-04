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
    '$2a$10$IzsA5Xcad9uCJH9WAIIX0OeUcD80.qrtVlxaAdNidpg/mCKGutFYK',
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);
