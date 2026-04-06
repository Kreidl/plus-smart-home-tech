DROP TABLE IF EXISTS order_products CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- создаём таблицу orders
CREATE TABLE IF NOT EXISTS orders (
    order_id            UUID PRIMARY KEY,
    state               VARCHAR(10),
    shopping_cart_id    UUID,
    payment_id          UUID,
    delivery_id         UUID,
    delivery_weight     DOUBLE PRECISION,
    delivery_volume     DOUBLE PRECISION,
    fragile             BOOLEAN,
    total_price         NUMERIC(10, 2),
    delivery_price      NUMERIC(10, 2),
    product_price       NUMERIC(10, 2)
);

-- создаём таблицу order_products
CREATE TABLE IF NOT EXISTS order_products (
    order_id        UUID REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id      UUID,
    quantity        BIGINT,
    PRIMARY KEY(order_id, product_id)
);