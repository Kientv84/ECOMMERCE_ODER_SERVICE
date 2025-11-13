-- ======================================
-- V1__create_order_tables.sql
-- ======================================

-- Extension for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Bảng shipping_methods_entity
DROP TABLE IF EXISTS shipping_methods_entity CASCADE;
CREATE TABLE IF NOT EXISTS shipping_methods_entity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipping_code VARCHAR(50),
    shipping_name VARCHAR(255),
    base_fee NUMERIC(18,2),
    description TEXT,
    status BOOLEAN,
    thumbnail_url TEXT,

    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- 2. Bảng order_entity
DROP TABLE IF EXISTS order_entity CASCADE;
CREATE TABLE IF NOT EXISTS order_entity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_code VARCHAR(50),
    user_id UUID NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    payment_status VARCHAR(50),
    status VARCHAR(50),
    total_price NUMERIC(18,2) NOT NULL,
    payment_id UUID,
    shipping_method_id UUID NOT NULL REFERENCES shipping_methods_entity(id) ON DELETE RESTRICT,
    shipping_address VARCHAR(500),

    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_order_user ON order_entity(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON order_entity(status);

-- 3. Bảng order_item_entity
DROP TABLE IF EXISTS order_item_entity CASCADE;
CREATE TABLE IF NOT EXISTS order_item_entity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES order_entity(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price NUMERIC(18,2) NOT NULL,
    quantity INT NOT NULL,
    line_total NUMERIC(18,2) NOT NULL,

    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_item_entity(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product ON order_item_entity(product_id);
