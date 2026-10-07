INSERT INTO order_items (order_id, product_id, sku, name, quantity, price, created_at, updated_at)
SELECT 'ORD-ID-1001', 'CLOTH-JEANS-003', 'CLOTH-JEANS-003', 'Straight-Leg Washed Jeans', 1, 299.00, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 'ORD-ID-1001');

INSERT INTO order_items (order_id, product_id, sku, name, quantity, price, created_at, updated_at)
SELECT 'ORD-ID-1002', 'CLOTH-TEE-001', 'CLOTH-TEE-001', 'Classic Cotton White T-Shirt', 1, 89.00, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 'ORD-ID-1002');

INSERT INTO order_items (order_id, product_id, sku, name, quantity, price, created_at, updated_at)
SELECT 'ORD-ID-2001', 'CLOTH-COAT-009', 'CLOTH-COAT-009', 'Wool Blend Coat', 1, 1299.00, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 'ORD-ID-2001');
