package com.inventory.inventory_service.dto;

public record InventoryReservationResponse(
        boolean reserved,
        String message
) {
}
