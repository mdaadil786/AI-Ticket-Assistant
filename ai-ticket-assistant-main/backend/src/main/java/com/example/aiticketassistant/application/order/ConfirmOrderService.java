package com.example.aiticketassistant.application.order;

import com.example.aiticketassistant.domain.catalog.Product;
import com.example.aiticketassistant.domain.catalog.ProductInventory;
import com.example.aiticketassistant.domain.catalog.ProductRepository;
import com.example.aiticketassistant.domain.customer.CustomerAddress;
import com.example.aiticketassistant.domain.customer.CustomerPaymentMethod;
import com.example.aiticketassistant.domain.customer.CustomerTrustRepository;
import com.example.aiticketassistant.domain.order.Order;
import com.example.aiticketassistant.domain.order.OrderConfirmation;
import com.example.aiticketassistant.domain.order.OrderConfirmationRepository;
import com.example.aiticketassistant.domain.order.OrderConfirmationStatus;
import com.example.aiticketassistant.domain.order.OrderItem;
import com.example.aiticketassistant.domain.order.OrderItemRepository;
import com.example.aiticketassistant.domain.order.OrderRepository;
import com.example.aiticketassistant.domain.shared.DomainException;
import com.example.aiticketassistant.domain.shared.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmOrderService {
    private final OrderConfirmationRepository confirmationRepository;
    private final ProductRepository productRepository;
    private final CustomerTrustRepository customerTrustRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ConfirmOrderService(OrderConfirmationRepository confirmationRepository,
                               ProductRepository productRepository,
                               CustomerTrustRepository customerTrustRepository,
                               OrderRepository orderRepository,
                               OrderItemRepository orderItemRepository) {
        this.confirmationRepository = confirmationRepository;
        this.productRepository = productRepository;
        this.customerTrustRepository = customerTrustRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderCreationResult confirm(String confirmationId, ConfirmOrderRequest request) {
        OrderConfirmation confirmation = loadOwnedPending(confirmationId, request);
        Instant now = Instant.now();
        if (confirmation.expired(now)) {
            confirmation.expire();
            confirmationRepository.save(confirmation);
            throw new DomainException("The order confirmation has expired. Please start the order again.");
        }
        Product product = productRepository.findById(confirmation.productId())
                .filter(Product::active)
                .orElseThrow(() -> new DomainException("Product not found or inactive: " + confirmation.productId()));
        ProductInventory inventory = productRepository.findInventory(product.id())
                .orElseThrow(() -> new DomainException("Product inventory not found: " + product.id()));
        BigDecimal currentTotal = product.price().multiply(BigDecimal.valueOf(confirmation.quantity()));
        if (product.price().compareTo(confirmation.unitPrice()) != 0 || currentTotal.compareTo(confirmation.totalAmount()) != 0) {
            confirmation.fail();
            confirmationRepository.save(confirmation);
            throw new DomainException("The product price has changed. Please review the order again.");
        }
        ProductInventory reserved = inventory.reserve(confirmation.quantity());
        CustomerAddress address = customerTrustRepository.findAddressForCustomer(confirmation.addressId(), confirmation.customerId())
                .orElseThrow(() -> new DomainException("The shipping address does not belong to the customer or is unavailable"));
        CustomerPaymentMethod paymentMethod = customerTrustRepository.findPaymentMethodForCustomer(confirmation.paymentMethodId(), confirmation.customerId())
                .orElseThrow(() -> new DomainException("The payment method does not belong to the customer or is unavailable"));

        String orderNo = "ORD-AI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = Order.assistantCreated(
                "ORD-ID-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                orderNo,
                confirmation.customerId(),
                confirmation.totalAmount(),
                address.id(),
                paymentMethod.id(),
                confirmation.id());
        Order saved = orderRepository.save(order);
        orderItemRepository.save(new OrderItem(saved.id().value(), product.id(), product.id(), product.name(), confirmation.quantity(), product.price()));
        productRepository.saveInventory(reserved);
        confirmation.confirm();
        confirmationRepository.save(confirmation);

        return new OrderCreationResult(
                saved.orderNo(),
                saved.orderStatus().name(),
                saved.paymentStatus().name(),
                saved.amount(),
                Map.of("productId", product.id(), "name", product.name(), "quantity", confirmation.quantity(), "unitPrice", product.price()),
                "Order created. No payment will be charged automatically; payment status remains " + saved.paymentStatus().name());
    }

    @Transactional
    public Map<String, Object> cancel(String confirmationId, ConfirmOrderRequest request) {
        OrderConfirmation confirmation = loadOwnedPending(confirmationId, request);
        confirmation.cancel();
        confirmationRepository.save(confirmation);
        return Map.of("confirmationId", confirmation.id(), "status", confirmation.status().name());
    }

    private OrderConfirmation loadOwnedPending(String confirmationId, ConfirmOrderRequest request) {
        OrderConfirmation confirmation = confirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new NotFoundException("Order confirmation not found: " + confirmationId));
        if (!confirmation.customerId().equals(request.customerId()) || !confirmation.sessionId().equals(request.sessionId())) {
            throw new DomainException("The order confirmation does not belong to the current customer or session");
        }
        if (confirmation.status() != OrderConfirmationStatus.PENDING) {
            throw new DomainException("The order confirmation is not actionable in its current state: " + confirmation.status());
        }
        return confirmation;
    }
}
