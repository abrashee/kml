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
    '$2b$10$omqdeIdcQbg.dkaPYxH2GOZ.AR4R3hlIQVrZ2PZaH5yntAiLy/Vz6',
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);
