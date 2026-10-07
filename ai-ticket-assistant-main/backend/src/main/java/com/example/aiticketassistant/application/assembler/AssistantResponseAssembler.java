package com.example.aiticketassistant.application.assembler;

import com.example.aiticketassistant.application.workflow.WorkflowContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AssistantResponseAssembler {
    public String assemble(WorkflowContext context, String orderAnalysis, String knowledgeAnswer) {
        String tools = context.toolResults().stream()
                .map(result -> "- " + result.tool() + ": " + (result.success() ? "succeeded" : "failed - " + result.error()))
                .collect(Collectors.joining("\n"));
        String ticket = context.recall("ticket") == null
                ? "No new ticket created"
                : "Ticket created/updated: " + context.recall("ticket");
        Object orderConfirmation = context.recall("orderConfirmation");
        Object productSearch = context.recall("productSearch");
        List<String> sections = new ArrayList<>();
        sections.add("Multi-Agent analysis is complete.");
        sections.add("**Intent**: %s (confidence %.2f)".formatted(
                context.intent() == null ? "GENERAL_SUPPORT" : context.intent().intent(),
                context.intent() == null ? 0.0 : context.intent().confidence()));
        if (orderAnalysis != null && !orderAnalysis.isBlank()) {
            sections.add("**Order Analysis**:\n" + orderAnalysis);
        }
        if (knowledgeAnswer != null && !knowledgeAnswer.isBlank()) {
            sections.add("**Knowledge Result**:\n" + knowledgeAnswer);
        }
        if (productSearch instanceof java.util.Map<?, ?> productData) {
            sections.add("**Product Search**:\n" + formatProducts(productData));
        }
        if (orderConfirmation != null) {
            sections.add("**Order Confirmation**:\nA pending order has been prepared. Review the product, amount, address, and payment method before creating the order. No payment will be charged automatically.");
        }
        sections.add("**Business Action**:\n" + ticket);
        sections.add("**Tool Trace**:\n" + (tools.isBlank() ? "- No tools called" : tools));
        return String.join("\n\n", sections) + "\n";
    }

    private String priceCondition(Object minPrice, Object maxPrice) {
        boolean hasMin = minPrice != null && !"unlimited".equals(String.valueOf(minPrice));
        boolean hasMax = maxPrice != null && !"unlimited".equals(String.valueOf(maxPrice));
        if (hasMin && hasMax) {
            return "above ¥" + minPrice + " and below ¥" + maxPrice;
        }
        if (hasMin) {
            return "above ¥" + minPrice;
        }
        if (hasMax) {
            return "below ¥" + maxPrice;
        }
        return "no price limit";
    }

    private String formatProducts(java.util.Map<?, ?> productData) {
        Object products = productData.get("products");
        Object minPrice = productData.get("minPrice");
        Object maxPrice = productData.get("maxPrice");
        if (Boolean.parseBoolean(String.valueOf(productData.get("combinationMode")))) {
            return formatMaxAffordableProducts(productData);
        }
        StringBuilder text = new StringBuilder("Matching products (")
                .append(priceCondition(minPrice, maxPrice))
                .append("):\n\n");
        if (products instanceof java.util.List<?> list && !list.isEmpty()) {
            for (Object item : list) {
                if (item instanceof java.util.Map<?, ?> product) {
                    text.append("- ")
                            .append(product.get("name"))
                            .append(" (")
                            .append(product.get("productId"))
                            .append("): ¥")
                            .append(product.get("price"))
                            .append("\n");
                }
            }
            text.append("\nYou can say: Buy one <product name>. The system will ask for confirmation before creating the order.");
            return text.toString().trim();
        }
        return "No clothing products matched the selected budget.";
    }

    private String formatMaxAffordableProducts(java.util.Map<?, ?> productData) {
        Object maxQuantity = productData.get("maxQuantity");
        if (maxQuantity == null || "0".equals(String.valueOf(maxQuantity))) {
            return "A budget of ¥%s is not enough to buy any clothing product.".formatted(productData.get("budget"));
        }
        return "Using the lowest-priced product, a budget of ¥%s can buy up to %s item(s) of %s (%s at ¥%s each), for a total of ¥%s with ¥%s remaining.\n\nYou can say: Buy %s of %s. The system will ask for confirmation before creating the order.".formatted(
                productData.get("budget"),
                productData.get("maxQuantity"),
                productData.get("bestProductName"),
                productData.get("bestProductId"),
                productData.get("bestProductPrice"),
                productData.get("totalPrice"),
                productData.get("remainingBudget"),
                productData.get("maxQuantity"),
                productData.get("bestProductName"));
    }
}
