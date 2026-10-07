CREATE TABLE products (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE product_inventory (
    product_id VARCHAR(64) PRIMARY KEY,
    stock_on_hand INT NOT NULL,
    reserved_stock INT NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_product_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE customer_addresses (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    recipient_name VARCHAR(128) NOT NULL,
    phone VARCHAR(64) NOT NULL,
    address_line VARCHAR(512) NOT NULL,
    is_default BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_customer_addresses_customer (customer_id)
);

CREATE TABLE customer_payment_methods (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    method_type VARCHAR(64) NOT NULL,
    display_label VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_customer_payment_methods_customer (customer_id)
);

CREATE TABLE order_confirmations (
    id VARCHAR(64) PRIMARY KEY,
    trace_id VARCHAR(128) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL,
    address_id VARCHAR(64) NOT NULL,
    payment_method_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_order_confirmations_session (session_id),
    INDEX idx_order_confirmations_customer (customer_id),
    CONSTRAINT fk_order_confirmations_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_order_confirmations_address FOREIGN KEY (address_id) REFERENCES customer_addresses(id),
    CONSTRAINT fk_order_confirmations_payment FOREIGN KEY (payment_method_id) REFERENCES customer_payment_methods(id)
);

ALTER TABLE orders
    ADD COLUMN address_id VARCHAR(64),
    ADD COLUMN payment_method_id VARCHAR(64),
    ADD COLUMN source VARCHAR(64),
    ADD COLUMN confirmation_id VARCHAR(64);

ALTER TABLE order_items
    ADD COLUMN product_id VARCHAR(64);

INSERT INTO products (id, name, price, active, created_at, updated_at)
VALUES
  ('CLOTH-TEE-001', 'Classic Cotton White T-Shirt', 99.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SHIRT-002', 'Blue Oxford Shirt', 199.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-JEANS-003', 'Straight-Leg Washed Jeans', 299.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-HOODIE-004', 'Black Hooded Sweatshirt', 259.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-DRESS-005', 'Floral Chiffon Dress', 329.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-JACKET-006', 'Lightweight Windbreaker Jacket', 399.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SKIRT-007', 'High-Waist A-Line Skirt', 189.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-POLO-008', 'Smart Casual Polo Shirt', 169.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-COAT-009', 'Wool Blend Coat', 699.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SWEATER-010', 'Beige Knit Sweater', 229.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO product_inventory (product_id, stock_on_hand, reserved_stock, updated_at)
VALUES
  ('CLOTH-TEE-001', 80, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SHIRT-002', 45, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-JEANS-003', 30, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-HOODIE-004', 36, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-DRESS-005', 24, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-JACKET-006', 18, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SKIRT-007', 40, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-POLO-008', 52, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-COAT-009', 12, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SWEATER-010', 28, 0, CURRENT_TIMESTAMP(6));

INSERT INTO customer_addresses (id, customer_id, recipient_name, phone, address_line, is_default, active, created_at, updated_at)
VALUES
  ('ADDR-CUST-1001-DEFAULT', 'CUST-1001', 'Alex Morgan', '138****1001', '18F, 100 Century Avenue, Pudong', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ADDR-CUST-2001-DEFAULT', 'CUST-2001', 'Jordan Lee', '138****2001', '6F, 88 Jianguo Road, Chaoyang', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO customer_payment_methods (id, customer_id, method_type, display_label, is_default, active, created_at, updated_at)
VALUES
  ('PAY-CUST-1001-DEFAULT', 'CUST-1001', 'CARD', 'Visa card **** 1001 (confirmation only; no automatic charge)', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('PAY-CUST-2001-DEFAULT', 'CUST-2001', 'ALIPAY', 'Alipay account 138****2001 (confirmation only; no automatic charge)', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
