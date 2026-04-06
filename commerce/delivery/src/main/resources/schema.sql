DROP TABLE IF EXISTS deliveries CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;

-- создаём таблицу addresses
CREATE TABLE IF NOT EXISTS addresses (
    address_id      UUID PRIMARY KEY,
    country         VARCHAR(50),
    city            VARCHAR(50),
    street          VARCHAR(50),
    house           VARCHAR(100),
    flat            VARCHAR(1000)
);

-- создаём таблицу deliveries
CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_address_id     UUID NOT NULL REFERENCES addresses(address_id) ON DELETE CASCADE,
    to_address_id       UUID NOT NULL REFERENCES addresses(address_id) ON DELETE CASCADE,
    order_id            UUID NOT NULL,
    delivery_state      VARCHAR(10)
);
