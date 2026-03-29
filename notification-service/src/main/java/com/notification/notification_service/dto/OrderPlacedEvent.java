package com.notification.notification_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderPlacedEvent(
        String orderNumber,
        List<OrderItemEvent> items,
        BigDecimal totalAmount,
        Instant createdAt
) {
}
