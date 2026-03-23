DROP TABLE IF EXISTS products CASCADE;

-- создаём таблицу products
CREATE TABLE IF NOT EXISTS products (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(100) NOT NULL,
    description       TEXT NOT NULL,
    image_src         TEXT,
    quantity_state    VARCHAR(50) NOT NULL,
    product_state     VARCHAR(50) NOT NULL,
    product_category  VARCHAR(50) NOT NULL,
    price             FLOAT NOT NULL
);