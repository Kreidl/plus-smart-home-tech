DROP TABLE IF EXISTS warehouse_reserved_products CASCADE;
DROP TABLE IF EXISTS warehouse_products CASCADE;

-- создаём таблицу warehouse_products
CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    width                   DOUBLE PRECISION NOT NULL,
    height                  DOUBLE PRECISION NOT NULL,
    depth                   DOUBLE PRECISION NOT NULL,
    weight                  DOUBLE PRECISION NOT NULL,
    fragile                 BOOLEAN NOT NULL,
    quantity                BIGINT NOT NULL,
);

CREATE TABLE IF NOT EXISTS warehouse_reserved_products (
    reserve_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id        UUID NOT NULL,
    product_id              UUID NOT NULL REFERENCES warehouse_products (product_id) ON DELETE CASCADE,
    reserved_quantity       BIGINT NOT NULL,
);