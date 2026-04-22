DROP TABLE IF EXISTS payments CASCADE;

-- создаём таблицу payments
CREATE TABLE IF NOT EXISTS payments (
    payment_id          UUID PRIMARY KEY,
    total_payment       NUMERIC(10, 2),
    delivery_total      NUMERIC(10, 2),
    fee_total           NUMERIC(10, 2),
    product_total       NUMERIC(10, 2),
    payment_state       VARCHAR(10),
    order_id            UUID
);