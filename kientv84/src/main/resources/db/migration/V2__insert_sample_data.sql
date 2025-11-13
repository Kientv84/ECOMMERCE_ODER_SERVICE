-- ======================================
-- V2__insert_sample_data.sql
-- ======================================

-- 1. Insert sample shipping methods
INSERT INTO shipping_methods_entity (id, shipping_code, shipping_name, base_fee, description, status, create_date, update_date)
VALUES
('c05fe63d-1843-45b8-b960-7d15c2b5d93f', 'GHN', 'Giao Hàng Nhanh', 35000.00, 'Dịch vụ giao hàng nhanh toàn quốc, thời gian giao 1-3 ngày.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('4e48bbb0-520d-420e-b57c-ffe7b8cb9048', 'INTERNAL', 'Giao Hàng Nội Bộ', 15000.00, 'Nhân viên của shop giao trực tiếp trong khu vực nội thành.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('6f31e7db-2afe-4a84-b3b9-04dea9ebec26', 'EXPRESS', 'Giao Hàng Hỏa Tốc', 50000.00, 'Giao siêu nhanh trong ngày (2-4 giờ).', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('778c8a6f-a2c9-4a27-98aa-3566c9d581d1', 'GHTK', 'Giao Hàng tiết kiệm', 30000.00, 'Dịch vụ giao hàng tiêu chuẩn, giá rẻ, thời gian giao 2-4 ngày.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Insert sample order
INSERT INTO order_entity (
    id, order_code, user_id, phone, email, payment_status, status, total_price, payment_id, shipping_method_id, shipping_address, create_date, update_date
)
VALUES (
    gen_random_uuid(),
    'ORD-0001',
    'c743008e-cd42-467e-bf10-35124b9a28b1',
    '0968727900',
    'truongchikien2021@example.com',
    'PENDING',
    'PROCESSING',
    4197000.00,
    '50aae9ae-ba5b-4627-ad7f-7546f5893bec',
    'c05fe63d-1843-45b8-b960-7d15c2b5d93f',
    '268/23 Lã Xuân Oai, Long Trường, Quận 9, Hồ Chí Minh',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 3. Insert sample order item
INSERT INTO order_item_entity (
    id, order_id, product_id, product_name, product_price, quantity, line_total, create_date, update_date
)
SELECT
    gen_random_uuid(),
    o.id,
    '6bd45b45-0124-4129-9b8d-a4259421c088',
    'Apex Seamless T-Shirt',
    499000.00,
    1,
    499000.00,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM order_entity o
WHERE o.order_code = 'ORD-0001';
