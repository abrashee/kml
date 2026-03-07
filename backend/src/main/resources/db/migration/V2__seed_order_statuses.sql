-- ===========================================
-- Flyway Migration: Seed Order Statuses
-- ===========================================

INSERT INTO order_statuses (name, description)
VALUES
    ('PENDING', 'Order has been created but not yet processed'),
    ('PROCESSING', 'Order is being processed'),
    ('COMPLETED', 'Order has been completed')
ON CONFLICT (name) DO NOTHING;