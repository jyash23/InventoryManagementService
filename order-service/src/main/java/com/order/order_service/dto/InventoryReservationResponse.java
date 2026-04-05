package com.order.order_service.dto;

public record InventoryReservationResponse(
        boolean reserved,
        String message
) {
}
