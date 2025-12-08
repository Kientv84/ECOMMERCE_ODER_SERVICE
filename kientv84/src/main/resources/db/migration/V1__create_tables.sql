-- ======================================
-- V1__create_tables.sql
-- ======================================
CREATE EXTENSION IF NOT EXISTS unaccent;

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

-- ===== ORDER SEARCH =====

ALTER TABLE order_entity
ADD COLUMN IF NOT EXISTS document_tsv tsvector;

UPDATE order_entity
SET document_tsv = to_tsvector(
     'simple',
     coalesce(public.unaccent(lower(order_code)),'') || ' ' ||
     coalesce(public.unaccent(lower(phone)),'') || ' ' ||
     coalesce(public.unaccent(lower(email)),'')
);

CREATE INDEX IF NOT EXISTS idx_order_document_tsv
ON order_entity USING GIN(document_tsv);

CREATE OR REPLACE FUNCTION order_tsv_trigger() RETURNS trigger AS $$
BEGIN
    NEW.document_tsv := to_tsvector(
        'simple',
        coalesce(public.unaccent(lower(NEW.order_code)),'') || ' ' ||
        coalesce(public.unaccent(lower(NEW.phone)),'') || ' ' ||
        coalesce(public.unaccent(lower(NEW.email)),'')
    );
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tsvectorupdate_order ON order_entity;

CREATE TRIGGER tsvectorupdate_order
BEFORE INSERT OR UPDATE ON order_entity
FOR EACH ROW
EXECUTE FUNCTION order_tsv_trigger();


-- Search suggest function
CREATE OR REPLACE FUNCTION order_search_suggest(input_text text, limit_count int DEFAULT 5)
RETURNS TABLE (
    order_id uuid,
    order_code text,
    phone text,
    email text,
    rank float
) AS $$
DECLARE
    q text;
BEGIN
    q := public.unaccent(lower(input_text));

    RETURN QUERY
    SELECT
        id AS order_id,
        order_code,
        phone,
        email,
        ts_rank(document_tsv, to_tsquery('simple', q || ':*')) AS rank
    FROM order_entity
    WHERE document_tsv @@ to_tsquery('simple', q || ':*')
    ORDER BY rank DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;


-- ===== SHIPPING METHOD SEARCH =====

ALTER TABLE shipping_methods_entity
ADD COLUMN IF NOT EXISTS document_tsv tsvector;

UPDATE shipping_methods_entity
SET document_tsv = to_tsvector(
     'simple',
     coalesce(public.unaccent(lower(shipping_code)),'') || ' ' ||
     coalesce(public.unaccent(lower(shipping_name)),'') || ' ' ||
     coalesce(public.unaccent(lower(description)),'')
);

CREATE INDEX IF NOT EXISTS idx_shipping_method_document_tsv
ON shipping_methods_entity USING GIN(document_tsv);

CREATE OR REPLACE FUNCTION shipping_method_tsv_trigger() RETURNS trigger AS $$
BEGIN
    NEW.document_tsv := to_tsvector(
        'simple',
        coalesce(public.unaccent(lower(NEW.shipping_code)),'') || ' ' ||
        coalesce(public.unaccent(lower(NEW.shipping_name)),'') || ' ' ||
        coalesce(public.unaccent(lower(NEW.description)),'')
    );
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tsvectorupdate_shipping_method ON shipping_methods_entity;

CREATE TRIGGER tsvectorupdate_shipping_method
BEFORE INSERT OR UPDATE ON shipping_methods_entity
FOR EACH ROW
EXECUTE FUNCTION shipping_method_tsv_trigger();


-- Search suggest function
CREATE OR REPLACE FUNCTION shipping_method_search_suggest(input_text text, limit_count int DEFAULT 5)
RETURNS TABLE (
    shipping_method_id uuid,
    shipping_code text,
    shipping_name text,
    description text,
    rank float
) AS $$
DECLARE
    q text;
BEGIN
    q := public.unaccent(lower(input_text));

    RETURN QUERY
    SELECT
        id AS shipping_method_id,
        shipping_code,
        shipping_name,
        description,
        ts_rank(document_tsv, to_tsquery('simple', q || ':*')) AS rank
    FROM shipping_methods_entity
    WHERE document_tsv @@ to_tsquery('simple', q || ':*')
    ORDER BY rank DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;
