package com.order.order_service.dto;

public record InventoryReservationRequest(
        String skuCode,
        Integer quantity
) {
}
