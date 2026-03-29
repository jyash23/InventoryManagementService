package com.order.order_service.dto;

import java.math.BigDecimal;

public record OrderItemEvent(
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}
