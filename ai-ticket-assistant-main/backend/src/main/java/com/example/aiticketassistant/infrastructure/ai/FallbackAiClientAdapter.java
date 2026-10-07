package com.example.aiticketassistant.infrastructure.ai;

import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "assistant.ai", name = "provider", havingValue = "fallback", matchIfMissing = true)
public class FallbackAiClientAdapter implements AiClientPort {
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("CLOTH-[A-Z]+-\\d{3}");

    @Override
    public AiResponse complete(AiRequest request) {
        String user = request.userPrompt() == null ? "" : request.userPrompt();
        String message = extractUserMessage(user);
        if (request.expectJson()) {
            if (isOrderListRequest(message)) {
                return AiResponse.fallback("""
                        {"intent":"QUERY_ORDER","title":"Order summary","priority":"MEDIUM","confidence":0.58,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}}]}
                        """);
            }
            String productId = extractProductId(message);
            if (hasPurchaseIntent(message) && productId != null) {
                return AiResponse.fallback("""
                        {"intent":"GENERAL_SUPPORT","title":"Order confirmation required","priority":"MEDIUM","confidence":0.6,"tool_calls":[{"tool":"CREATE_ORDER","arguments":{"productId":"%s","quantity":%d}}]}
                        """.formatted(productId, extractQuantity(message)));
            }
            if (hasPurchaseIntent(message)) {
                return AiResponse.fallback("""
                        {"intent":"GENERAL_SUPPORT","title":"Product selection required","priority":"MEDIUM","confidence":0.5,"tool_calls":[]}
                        """);
            }
            if ((message.contains("支付") && message.contains("工单")) || (message.toLowerCase().contains("payment") && message.toLowerCase().contains("ticket"))) {
                return AiResponse.fallback("""
                        {"intent":"CREATE_TICKET","title":"Payment failed","priority":"HIGH","confidence":0.62,"tool_calls":[{"tool":"CREATE_TICKET","arguments":{"title":"Payment failed","description":"Customer reported a payment failure","priority":"HIGH"}}]}
                        """);
            }
            if ((message.contains("订单") && (message.contains("怎么办") || message.contains("没生成"))) || (message.toLowerCase().contains("order") && (message.toLowerCase().contains("what should i do") || message.toLowerCase().contains("not created") || message.toLowerCase().contains("was not created")))) {
                return AiResponse.fallback("""
                        {"intent":"ORDER_AND_KNOWLEDGE","title":"Payment succeeded but order was not created","priority":"HIGH","confidence":0.61,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}},{"tool":"SEARCH_KNOWLEDGE","arguments":{"query":"My payment succeeded but the order was not created. What should I do?","limit":5}}]}
                        """);
            }
            if (message.contains("订单") || message.contains("ORD-") || message.toLowerCase().contains("order")) {
                return AiResponse.fallback("""
                        {"intent":"QUERY_ORDER","title":"Order status inquiry","priority":"MEDIUM","confidence":0.58,"tool_calls":[{"tool":"QUERY_ORDER","arguments":{}}]}
                        """);
            }
            if (message.contains("退款") || message.contains("怎么") || message.contains("FAQ") || message.contains("知识") || message.toLowerCase().contains("refund") || message.toLowerCase().contains("how do") || message.toLowerCase().contains("knowledge")) {
                return AiResponse.fallback("""
                        {"intent":"KNOWLEDGE_QA","title":"Knowledge inquiry","priority":"MEDIUM","confidence":0.55,"tool_calls":[{"tool":"SEARCH_KNOWLEDGE","arguments":{"limit":5}}]}
                        """);
            }
            return AiResponse.fallback("""
                    {"intent":"GENERAL_SUPPORT","title":"AI support inquiry","priority":"MEDIUM","confidence":0.5,"tool_calls":[]}
                    """);
        }
        if ("OrderAgent".equals(request.agentName())) {
            return AiResponse.fallback(orderFallback(user));
        }
        if ("KnowledgeAgent".equals(request.agentName())) {
            return AiResponse.fallback(knowledgeFallback(user));
        }
        if ("DirectQwen".equals(request.agentName())) {
            return AiResponse.fallback(directFallback(message));
        }
        return AiResponse.fallback("AI service is not configured. Using local rule-based fallback response.");
    }

    private boolean isOrderListRequest(String message) {
        String value = message == null ? "" : message;
        return value.contains("几个订单")
                || value.contains("多少订单")
                || value.contains("一共") && value.contains("订单")
                || value.contains("所有订单")
                || value.contains("全部订单")
                || value.contains("我的订单")
                || value.contains("买了") && value.contains("订单")
                || value.contains("展示") && value.contains("订单")
                || value.toLowerCase().contains("my orders")
                || value.toLowerCase().contains("how many orders")
                || value.toLowerCase().contains("show my orders")
                || value.toLowerCase().contains("all orders");
    }

    private int extractBudget(String message) {
        Matcher matcher = Pattern.compile("(?i)(\\d+)\\s*(?:yuan|rmb|¥)?").matcher(message == null ? "" : message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 500;
    }

    private boolean hasPurchaseIntent(String message) {
        return message.toLowerCase().contains("buy ") || message.toLowerCase().startsWith("buy") || message.toLowerCase().contains("purchase") || message.toLowerCase().contains("place an order") || message.toLowerCase().contains("order ") || message.contains("买") || message.contains("购买") || message.contains("下单");
    }

    private String extractProductId(String message) {
        String normalized = message == null ? "" : message;
        Matcher matcher = PRODUCT_ID_PATTERN.matcher(normalized);
        if (matcher.find()) {
            return matcher.group();
        }
        return productIdFromName(normalized);
    }

    private String productIdFromName(String message) {
        if (message.toLowerCase().contains("classic cotton white t-shirt") || message.toLowerCase().contains("white t-shirt") || message.contains("白色T恤") || message.contains("纯棉T恤") || message.contains("T恤")) return "CLOTH-TEE-001";
        if (message.toLowerCase().contains("blue oxford shirt") || message.toLowerCase().contains("oxford shirt") || message.contains("牛津纺衬衫") || message.contains("蓝色衬衫") || message.contains("衬衫")) return "CLOTH-SHIRT-002";
        if (message.toLowerCase().contains("straight-leg washed jeans") || message.toLowerCase().contains("washed jeans") || message.toLowerCase().contains("jeans") || message.contains("水洗牛仔裤") || message.contains("直筒牛仔裤") || message.contains("牛仔裤")) return "CLOTH-JEANS-003";
        if (message.toLowerCase().contains("black hooded sweatshirt") || message.toLowerCase().contains("black hoodie") || message.toLowerCase().contains("hoodie") || message.contains("连帽卫衣") || message.contains("黑色卫衣") || message.contains("卫衣")) return "CLOTH-HOODIE-004";
        if (message.toLowerCase().contains("floral chiffon dress") || message.toLowerCase().contains("chiffon dress") || message.toLowerCase().contains("dress") || message.contains("雪纺连衣裙") || message.contains("碎花连衣裙") || message.contains("连衣裙")) return "CLOTH-DRESS-005";
        if (message.toLowerCase().contains("lightweight windbreaker jacket") || message.toLowerCase().contains("windbreaker") || message.contains("防风夹克") || message.contains("轻薄夹克") || message.contains("夹克")) return "CLOTH-JACKET-006";
        if (message.toLowerCase().contains("high-waist a-line skirt") || message.toLowerCase().contains("a-line skirt") || message.contains("A字半身裙") || message.contains("半身裙")) return "CLOTH-SKIRT-007";
        if (message.toLowerCase().contains("smart casual polo shirt") || message.toLowerCase().contains("polo shirt") || message.contains("Polo衫") || message.contains("POLO衫") || message.contains("polo衫")) return "CLOTH-POLO-008";
        if (message.toLowerCase().contains("wool blend coat") || message.toLowerCase().contains("wool coat") || message.contains("羊毛混纺大衣") || message.contains("羊毛大衣") || message.contains("大衣")) return "CLOTH-COAT-009";
        if (message.toLowerCase().contains("beige knit sweater") || message.toLowerCase().contains("knit sweater") || message.toLowerCase().contains("sweater") || message.contains("针织毛衣") || message.contains("米色毛衣") || message.contains("毛衣")) return "CLOTH-SWEATER-010";
        return null;
    }

    private int extractQuantity(String message) {
        Matcher matcher = Pattern.compile("(?i)(?:buy|purchase|order)\\s*(\\d+)\\s*(?:items?|pcs?|units?)").matcher(message == null ? "" : message);
        if (matcher.find()) {
            return Math.max(1, Integer.parseInt(matcher.group(1)));
        }
        return 1;
    }

    private String extractUserMessage(String userPrompt) {
        String marker = "User message:";
        int start = userPrompt.indexOf(marker);
        if (start < 0) {
            return userPrompt;
        }
        int valueStart = start + marker.length();
        int end = userPrompt.indexOf('\n', valueStart);
        return (end < 0 ? userPrompt.substring(valueStart) : userPrompt.substring(valueStart, end)).trim();
    }

    private String directFallback(String message) {
        if (isMaxCountClothingQuestion(message)) {
            int budget = extractBudget(message);
            int unitPrice = 109;
            int quantity = budget / unitPrice;
            int total = quantity * unitPrice;
            int remaining = budget - total;
            return "With a budget of %d, you can buy up to %d T-shirt(s) at %d each, for a total of %d, leaving %d.".formatted(budget, quantity, unitPrice, total, remaining);
        }
        if ((message.contains("支付") && message.contains("订单") && (message.contains("没生成") || message.contains("怎么办"))) || (message.toLowerCase().contains("payment") && message.toLowerCase().contains("order") && (message.toLowerCase().contains("not created") || message.toLowerCase().contains("what should i do")))) {
            return "First confirm whether the payment was charged and keep the payment receipt. If the order is still missing, contact support to verify the payment record and request a replacement order or refund if needed.";
        }
        if ((message.contains("退款") && message.contains("怎么")) || (message.toLowerCase().contains("refund") && message.toLowerCase().contains("how"))) {
            return "Open the order details and use the refund option when available. Submit the reason and supporting evidence and wait for review. If no refund option is available, contact support.";
        }
        return "This is a general question I can answer directly. Requests involving your order, account, or actions such as ordering or refunds must go through the business workflow.";
    }

    private boolean isMaxCountClothingQuestion(String message) {
        String value = message == null ? "" : message;
        return (value.contains("衣服") || value.contains("T恤") || value.contains("服装") || value.toLowerCase().contains("clothing") || value.toLowerCase().contains("t-shirt") || value.toLowerCase().contains("shirt"))
                && (value.contains("最多") || value.contains("几件") || value.contains("多少件") || value.toLowerCase().contains("how many") || value.toLowerCase().contains("most") || value.toLowerCase().contains("budget"));
    }

    private String orderFallback(String userPrompt) {
        if (userPrompt.contains("totalOrders=") && userPrompt.contains("orders=")) {
            return orderListFallback(userPrompt);
        }
        if (userPrompt.contains("Order tool result: No order context found")) {
            return "No order context was found, so the order status cannot be determined. Provide an order number or run an order query first.";
        }
        if (userPrompt.contains("orderStatus=FAILED") || userPrompt.contains("订单生成失败")) {
            return "The order query shows that payment succeeded but the order is in an abnormal state. Verify the payment record and start the compensation workflow.";
        }
        if (userPrompt.contains("paymentStatus=PAID")) {
            return "The order is paid. Review the order status to determine whether a replacement order, refund, or manual review is required.";
        }
        return "Order context has been loaded. Choose the next action based on the current order status.";
    }

    private String orderListFallback(String userPrompt) {
        Matcher count = Pattern.compile("totalOrders=(\\d+)").matcher(userPrompt);
        String total = count.find() ? count.group(1) : "多";
        StringBuilder answer = new StringBuilder("You currently have ").append(total).append(" order(s).\n");
        Matcher orderMatcher = Pattern.compile("orderNo=([^,}]+).*?amount=([0-9.]+).*?orderStatus=([A-Z_]+).*?paymentStatus=([A-Z_]+)").matcher(userPrompt);
        while (orderMatcher.find()) {
            answer.append("- ")
                    .append(orderMatcher.group(1).trim())
                    .append(" - Amount ¥")
                    .append(orderMatcher.group(2).trim())
                    .append(" - order status ")
                    .append(orderMatcher.group(3).trim())
                    .append(" - payment status ")
                    .append(orderMatcher.group(4).trim())
                    .append("\n");
        }
        return answer.toString().trim();
    }

    private String knowledgeFallback(String userPrompt) {
        if (userPrompt.contains("Retrieved sources:\n") && userPrompt.trim().endsWith("Retrieved sources:")) {
            return "No directly matching knowledge was found. Consider manual review or provide more context.";
        }
        if (userPrompt.contains("退款") || userPrompt.toLowerCase().contains("refund")) {
            return "Knowledge base guidance: For refunds, first confirm payment status, refund channel, and expected settlement time.";
        }
        if (userPrompt.contains("支付") || userPrompt.toLowerCase().contains("payment")) {
            return "Knowledge base guidance: Keep the payment receipt, verify the payment record, and then determine whether to replace the order or issue a refund.";
        }
        return "Knowledge base guidance: Use the retrieved information to handle the request. Provide more context when the available information is insufficient.";
    }
}
