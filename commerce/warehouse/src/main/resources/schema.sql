DROP TABLE IF EXISTS warehouse_booking_products CASCADE;
DROP TABLE IF EXISTS warehouse_products CASCADE;

-- создаём таблицу warehouse_products
CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id              UUID PRIMARY KEY,
    width                   DOUBLE PRECISION NOT NULL,
    height                  DOUBLE PRECISION NOT NULL,
    depth                   DOUBLE PRECISION NOT NULL,
    weight                  DOUBLE PRECISION NOT NULL,
    fragile                 BOOLEAN NOT NULL,
    quantity                BIGINT NOT NULL
);

-- создаём таблицу warehouse_booking_products
CREATE TABLE IF NOT EXISTS warehouse_booking_products (
    booking_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id           UUID NOT NULL,
    product_id         UUID NOT NULL REFERENCES warehouse_products (product_id) ON DELETE CASCADE,
    booked_quantity    BIGINT NOT NULL,
    delivery_id        UUID
);