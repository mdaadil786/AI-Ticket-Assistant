INSERT INTO orders (id, order_no, customer_id, amount, order_status, payment_status, created_at, updated_at)
VALUES
  ('ORD-ID-1001', 'ORD-1001', 'CUST-1001', 299.00, 'FAILED', 'PAID', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ORD-ID-1002', 'ORD-1002', 'CUST-1001', 89.00, 'SHIPPED', 'PAID', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ORD-ID-2001', 'ORD-2001', 'CUST-2001', 1299.00, 'FAILED', 'FAILED', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO support_agents (id, name, role, active, created_at, updated_at)
VALUES
  ('AGT-PAY-01', 'Payment Human Support', 'HUMAN_SUPPORT', true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('AGT-ORD-01', 'Order Human Support', 'HUMAN_SUPPORT', true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO agent_capabilities (agent_id, capability)
VALUES
  ('AGT-PAY-01', 'PAYMENT'),
  ('AGT-PAY-01', 'REFUND'),
  ('AGT-ORD-01', 'ORDER'),
  ('AGT-ORD-01', 'LOGISTICS');

INSERT INTO knowledge_documents (id, title, category, source, content, updated_at)
VALUES
  ('DOC-PAY-001', 'Payment Succeeded but Order Was Not Created - Handling Guide', 'PAYMENT', 'internal://kb/payment-order-missing', 'When payment succeeds but an order is not created, first query the order and payment record. If the order status is FAILED and the payment status is PAID, create a high-priority payment support ticket, retain the payment receipt, and route the case to the payment support team for compensation processing.', CURRENT_TIMESTAMP(6)),
  ('DOC-PAY-002', 'Payment Failure Ticket Creation FAQ', 'PAYMENT', 'internal://faq/payment-failed-ticket', 'For payment failures, collect the customer ID, order number, payment channel, and failure screenshot. High-value orders or repeated failures should be marked HIGH priority.', CURRENT_TIMESTAMP(6)),
  ('DOC-ORDER-001', 'Order Status Guide', 'ORDER', 'internal://kb/order-status', 'Order statuses include CREATED, PAID, FULFILLING, SHIPPED, DELIVERED, FAILED, and CANCELLED. FAILED means support review is required.', CURRENT_TIMESTAMP(6));

INSERT INTO knowledge_chunks (id, document_id, title, category, source, content)
VALUES
  ('CHK-PAY-001-1', 'DOC-PAY-001', 'Payment Succeeded but Order Was Not Created - Handling Guide', 'PAYMENT', 'internal://kb/payment-order-missing#1', 'Payment succeeded but the order was not created: first query the order and payment record; if payment is PAID and the order is FAILED, create a high-priority payment ticket and start the compensation workflow.'),
  ('CHK-PAY-002-1', 'DOC-PAY-002', 'Payment Failure Ticket Creation FAQ', 'PAYMENT', 'internal://faq/payment-failed-ticket#1', 'Creating a payment failure ticket requires a title, customer ID, order number, payment channel, and failure screenshot; the priority is usually HIGH.'),
  ('CHK-ORDER-001-1', 'DOC-ORDER-001', 'Order Status Guide', 'ORDER', 'internal://kb/order-status#1', 'Order FAILED means the business process failed; payment PAID plus order FAILED is a typical replacement-order or refund verification scenario.');
