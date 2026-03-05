-- V2__seed_order_statuses.sql
INSERT INTO order_statuses (name, description)
VALUES
    ('PENDING', 'Order has been created but not yet processed'),
    ('PROCESSING', 'Order is being processed'),
    ('COMPLETED', 'Order has been completed')
ON CONFLICT (name) DO NOTHING;