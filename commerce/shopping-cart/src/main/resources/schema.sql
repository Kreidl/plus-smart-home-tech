DROP TABLE IF EXISTS products CASCADE;

-- создаём таблицу carts
CREATE TABLE IF NOT EXISTS carts (
    cart_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username          VARCHAR(100) NOT NULL,
    activated         BOOLEAN NOT NULL DEFAULT true
);

-- создаём таблицу cart_products
CREATE TABLE IF NOT EXISTS cart_products (
    cart_id           UUID NOT NULL REFERENCES shopping_carts(cart_id) ON DELETE CASCADE,
    product_id        UUID NOT NULL,
    quantity          INT NOT NULL,
    PRIMARY KEY(cart_id, product_id)
);