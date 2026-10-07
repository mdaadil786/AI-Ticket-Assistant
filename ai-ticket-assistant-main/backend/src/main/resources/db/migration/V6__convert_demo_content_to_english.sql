-- Convert demo content to English so the default application experience is English-only.
UPDATE products SET name = CASE id
    WHEN 'CLOTH-TEE-001' THEN 'Classic Cotton White T-Shirt'
    WHEN 'CLOTH-SHIRT-002' THEN 'Blue Oxford Shirt'
    WHEN 'CLOTH-JEANS-003' THEN 'Straight-Leg Washed Jeans'
    WHEN 'CLOTH-HOODIE-004' THEN 'Black Hooded Sweatshirt'
    WHEN 'CLOTH-DRESS-005' THEN 'Floral Chiffon Dress'
    WHEN 'CLOTH-JACKET-006' THEN 'Lightweight Windbreaker Jacket'
    WHEN 'CLOTH-SKIRT-007' THEN 'High-Waist A-Line Skirt'
    WHEN 'CLOTH-POLO-008' THEN 'Smart Casual Polo Shirt'
    WHEN 'CLOTH-COAT-009' THEN 'Wool Blend Coat'
    WHEN 'CLOTH-SWEATER-010' THEN 'Beige Knit Sweater'
    ELSE name END;

UPDATE order_items SET name = CASE product_id
    WHEN 'CLOTH-TEE-001' THEN 'Classic Cotton White T-Shirt'
    WHEN 'CLOTH-SHIRT-002' THEN 'Blue Oxford Shirt'
    WHEN 'CLOTH-JEANS-003' THEN 'Straight-Leg Washed Jeans'
    WHEN 'CLOTH-HOODIE-004' THEN 'Black Hooded Sweatshirt'
    WHEN 'CLOTH-DRESS-005' THEN 'Floral Chiffon Dress'
    WHEN 'CLOTH-JACKET-006' THEN 'Lightweight Windbreaker Jacket'
    WHEN 'CLOTH-SKIRT-007' THEN 'High-Waist A-Line Skirt'
    WHEN 'CLOTH-POLO-008' THEN 'Smart Casual Polo Shirt'
    WHEN 'CLOTH-COAT-009' THEN 'Wool Blend Coat'
    WHEN 'CLOTH-SWEATER-010' THEN 'Beige Knit Sweater'
    ELSE name END;

UPDATE customer_addresses SET recipient_name = CASE customer_id
    WHEN 'CUST-1001' THEN 'Alex Morgan'
    WHEN 'CUST-2001' THEN 'Jordan Lee'
    ELSE recipient_name END,
    address_line = CASE customer_id
    WHEN 'CUST-1001' THEN '18F, 100 Century Avenue, Pudong'
    WHEN 'CUST-2001' THEN '6F, 88 Jianguo Road, Chaoyang'
    ELSE address_line END;

UPDATE customer_payment_methods SET display_label = CASE customer_id
    WHEN 'CUST-1001' THEN 'Visa card **** 1001 (confirmation only; no automatic charge)'
    WHEN 'CUST-2001' THEN 'Alipay account 138****2001 (confirmation only; no automatic charge)'
    ELSE display_label END;

UPDATE knowledge_documents SET title = CASE id
    WHEN 'DOC-PAY-001' THEN 'Payment Succeeded but Order Was Not Created - Handling Guide'
    WHEN 'DOC-PAY-002' THEN 'Payment Failure Ticket Creation FAQ'
    WHEN 'DOC-ORDER-001' THEN 'Order Status Guide'
    WHEN 'DOC-CATALOG-CLOTHING' THEN 'Clothing Product Catalog'
    ELSE title END,
    content = CASE id
    WHEN 'DOC-PAY-001' THEN 'When payment succeeds but an order is not created, first query the order and payment record. If the order status is FAILED and the payment status is PAID, create a high-priority payment support ticket, retain the payment receipt, and route the case to the payment support team for compensation processing.'
    WHEN 'DOC-PAY-002' THEN 'For payment failures, collect the customer ID, order number, payment channel, and failure screenshot. High-value orders or repeated failures should be marked HIGH priority.'
    WHEN 'DOC-ORDER-001' THEN 'Order statuses include CREATED, PAID, FULFILLING, SHIPPED, DELIVERED, FAILED, and CANCELLED. FAILED means support review is required.'
    WHEN 'DOC-CATALOG-CLOTHING' THEN 'Product ID, product name, and price: CLOTH-TEE-001 Classic Cotton White T-Shirt ¥99.00; CLOTH-SHIRT-002 Blue Oxford Shirt ¥199.00; CLOTH-JEANS-003 Straight-Leg Washed Jeans ¥299.00; CLOTH-HOODIE-004 Black Hooded Sweatshirt ¥259.00; CLOTH-DRESS-005 Floral Chiffon Dress ¥329.00; CLOTH-JACKET-006 Lightweight Windbreaker Jacket ¥399.00; CLOTH-SKIRT-007 High-Waist A-Line Skirt ¥189.00; CLOTH-POLO-008 Smart Casual Polo Shirt ¥169.00; CLOTH-COAT-009 Wool Blend Coat ¥699.00; CLOTH-SWEATER-010 Beige Knit Sweater ¥229.00.'
    ELSE content END;

UPDATE knowledge_chunks SET title = CASE id
    WHEN 'CHK-PAY-001-1' THEN 'Payment Succeeded but Order Was Not Created - Handling Guide'
    WHEN 'CHK-PAY-002-1' THEN 'Payment Failure Ticket Creation FAQ'
    WHEN 'CHK-ORDER-001-1' THEN 'Order Status Guide'
    WHEN 'CHK-CATALOG-CLOTHING-TEE-001' THEN 'Clothing Catalog: Classic Cotton White T-Shirt'
    WHEN 'CHK-CATALOG-CLOTHING-SHIRT-002' THEN 'Clothing Catalog: Blue Oxford Shirt'
    WHEN 'CHK-CATALOG-CLOTHING-JEANS-003' THEN 'Clothing Catalog: Straight-Leg Washed Jeans'
    WHEN 'CHK-CATALOG-CLOTHING-HOODIE-004' THEN 'Clothing Catalog: Black Hooded Sweatshirt'
    WHEN 'CHK-CATALOG-CLOTHING-DRESS-005' THEN 'Clothing Catalog: Floral Chiffon Dress'
    WHEN 'CHK-CATALOG-CLOTHING-JACKET-006' THEN 'Clothing Catalog: Lightweight Windbreaker Jacket'
    WHEN 'CHK-CATALOG-CLOTHING-SKIRT-007' THEN 'Clothing Catalog: High-Waist A-Line Skirt'
    WHEN 'CHK-CATALOG-CLOTHING-POLO-008' THEN 'Clothing Catalog: Smart Casual Polo Shirt'
    WHEN 'CHK-CATALOG-CLOTHING-COAT-009' THEN 'Clothing Catalog: Wool Blend Coat'
    WHEN 'CHK-CATALOG-CLOTHING-SWEATER-010' THEN 'Clothing Catalog: Beige Knit Sweater'
    WHEN 'CHK-CATALOG-CLOTHING-ALL' THEN 'Clothing Catalog: Complete Price List'
    ELSE title END,
    content = CASE id
    WHEN 'CHK-PAY-001-1' THEN 'Payment succeeded but the order was not created: first query the order and payment record; if payment is PAID and the order is FAILED, create a high-priority payment ticket and start the compensation workflow.'
    WHEN 'CHK-PAY-002-1' THEN 'Creating a payment failure ticket requires a title, customer ID, order number, payment channel, and failure screenshot; the priority is usually HIGH.'
    WHEN 'CHK-ORDER-001-1' THEN 'Order FAILED means the business process failed; payment PAID plus order FAILED is a typical replacement-order or refund verification scenario.'
    WHEN 'CHK-CATALOG-CLOTHING-TEE-001' THEN 'Product ID CLOTH-TEE-001, product name Classic Cotton White T-Shirt, clothing product, price ¥99.00.'
    WHEN 'CHK-CATALOG-CLOTHING-SHIRT-002' THEN 'Product ID CLOTH-SHIRT-002, product name Blue Oxford Shirt, clothing product, price ¥199.00.'
    WHEN 'CHK-CATALOG-CLOTHING-JEANS-003' THEN 'Product ID CLOTH-JEANS-003, product name Straight-Leg Washed Jeans, clothing product, price ¥299.00.'
    WHEN 'CHK-CATALOG-CLOTHING-HOODIE-004' THEN 'Product ID CLOTH-HOODIE-004, product name Black Hooded Sweatshirt, clothing product, price ¥259.00.'
    WHEN 'CHK-CATALOG-CLOTHING-DRESS-005' THEN 'Product ID CLOTH-DRESS-005, product name Floral Chiffon Dress, clothing product, price ¥329.00.'
    WHEN 'CHK-CATALOG-CLOTHING-JACKET-006' THEN 'Product ID CLOTH-JACKET-006, product name Lightweight Windbreaker Jacket, clothing product, price ¥399.00.'
    WHEN 'CHK-CATALOG-CLOTHING-SKIRT-007' THEN 'Product ID CLOTH-SKIRT-007, product name High-Waist A-Line Skirt, clothing product, price ¥189.00.'
    WHEN 'CHK-CATALOG-CLOTHING-POLO-008' THEN 'Product ID CLOTH-POLO-008, product name Smart Casual Polo Shirt, clothing product, price ¥169.00.'
    WHEN 'CHK-CATALOG-CLOTHING-COAT-009' THEN 'Product ID CLOTH-COAT-009, product name Wool Blend Coat, clothing product, price ¥699.00.'
    WHEN 'CHK-CATALOG-CLOTHING-SWEATER-010' THEN 'Product ID CLOTH-SWEATER-010, product name Beige Knit Sweater, clothing product, price ¥229.00.'
    WHEN 'CHK-CATALOG-CLOTHING-ALL' THEN 'Complete clothing catalog: Classic Cotton White T-Shirt CLOTH-TEE-001 ¥99.00; Blue Oxford Shirt CLOTH-SHIRT-002 ¥199.00; Straight-Leg Washed Jeans CLOTH-JEANS-003 ¥299.00; Black Hooded Sweatshirt CLOTH-HOODIE-004 ¥259.00; Floral Chiffon Dress CLOTH-DRESS-005 ¥329.00; Lightweight Windbreaker Jacket CLOTH-JACKET-006 ¥399.00; High-Waist A-Line Skirt CLOTH-SKIRT-007 ¥189.00; Smart Casual Polo Shirt CLOTH-POLO-008 ¥169.00; Wool Blend Coat CLOTH-COAT-009 ¥699.00; Beige Knit Sweater CLOTH-SWEATER-010 ¥229.00.'
    ELSE content END;
