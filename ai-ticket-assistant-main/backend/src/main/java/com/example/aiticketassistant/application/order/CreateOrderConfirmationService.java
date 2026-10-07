package com.example.aiticketassistant.application.order;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.domain.catalog.Product;
import com.example.aiticketassistant.domain.catalog.ProductInventory;
import com.example.aiticketassistant.domain.catalog.ProductRepository;
import com.example.aiticketassistant.domain.customer.CustomerAddress;
import com.example.aiticketassistant.domain.customer.CustomerPaymentMethod;
import com.example.aiticketassistant.domain.customer.CustomerTrustRepository;
import com.example.aiticketassistant.domain.order.OrderConfirmation;
import com.example.aiticketassistant.domain.order.OrderConfirmationRepository;
import com.example.aiticketassistant.domain.order.OrderConfirmationStatus;
import com.example.aiticketassistant.domain.shared.DomainException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderConfirmationService {
    private static final Duration CONFIRMATION_TTL = Duration.ofMinutes(10);

    private final ProductRepository productRepository;
    private final CustomerTrustRepository customerTrustRepository;
    private final OrderConfirmationRepository confirmationRepository;

    public CreateOrderConfirmationService(ProductRepository productRepository,
                                          CustomerTrustRepository customerTrustRepository,
                                          OrderConfirmationRepository confirmationRepository) {
        this.productRepository = productRepository;
        this.customerTrustRepository = customerTrustRepository;
        this.confirmationRepository = confirmationRepository;
    }

    @Transactional
    public OrderConfirmationView create(WorkflowContext context, String productId, int quantity, String addressId, String paymentMethodId) {
        if (productId == null || productId.isBlank()) {
            throw new DomainException("Please provide a valid product ID. The AI must not invent products.");
        }
        if (quantity <= 0) {
            throw new DomainException("Quantity must be greater than 0");
        }
        Product product = productRepository.findById(productId)
                .filter(Product::active)
                .orElseThrow(() -> new DomainException("Product not found or inactive: " + productId));
        ProductInventory inventory = productRepository.findInventory(product.id())
                .orElseThrow(() -> new DomainException("Product inventory not found: " + product.id()));
        if (inventory.availableStock() < quantity) {
            throw new DomainException("Insufficient inventory for product: " + product.name());
        }
        CustomerAddress address = resolveAddress(context.customerId(), addressId);
        CustomerPaymentMethod paymentMethod = resolvePaymentMethod(context.customerId(), paymentMethodId);
        BigDecimal totalAmount = product.price().multiply(BigDecimal.valueOf(quantity));
        Instant now = Instant.now();
        OrderConfirmation confirmation = new OrderConfirmation(
                "OCF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                context.trace().traceId(),
                context.sessionId(),
                context.customerId(),
                product.id(),
                quantity,
                product.price(),
                totalAmount,
                address.id(),
                paymentMethod.id(),
                OrderConfirmationStatus.PENDING,
                now.plus(CONFIRMATION_TTL),
                now,
                now);
        OrderConfirmation saved = confirmationRepository.save(confirmation);
        return toView(saved, product, address, paymentMethod);
    }

    public OrderConfirmationView toView(OrderConfirmation confirmation, Product product, CustomerAddress address, CustomerPaymentMethod paymentMethod) {
        return new OrderConfirmationView(
                confirmation.id(),
                confirmation.sessionId(),
                confirmation.customerId(),
                "Confirm purchase for ¥" + confirmation.totalAmount().stripTrailingZeros().toPlainString() + "?",
                product.id(),
                product.name(),
                confirmation.quantity(),
                confirmation.unitPrice(),
                confirmation.totalAmount(),
                Map.of("id", address.id(), "display", address.display()),
                Map.of("id", paymentMethod.id(), "display", paymentMethod.displayLabel(), "methodType", paymentMethod.methodType()),
                confirmation.expiresAt());
    }

    private CustomerAddress resolveAddress(String customerId, String addressId) {
        if (addressId != null && !addressId.isBlank()) {
            return customerTrustRepository.findAddressForCustomer(addressId, customerId)
                    .orElseThrow(() -> new DomainException("The shipping address does not belong to the customer or is unavailable"));
        }
        return customerTrustRepository.findDefaultAddress(customerId)
                .orElseThrow(() -> new DomainException("No trusted default shipping address is available for the customer"));
    }

    private CustomerPaymentMethod resolvePaymentMethod(String customerId, String paymentMethodId) {
        if (paymentMethodId != null && !paymentMethodId.isBlank()) {
            return customerTrustRepository.findPaymentMethodForCustomer(paymentMethodId, customerId)
                    .orElseThrow(() -> new DomainException("The payment method does not belong to the customer or is unavailable"));
        }
        return customerTrustRepository.findDefaultPaymentMethod(customerId)
                .orElseThrow(() -> new DomainException("No trusted default payment method is available for the customer"));
    }
}
