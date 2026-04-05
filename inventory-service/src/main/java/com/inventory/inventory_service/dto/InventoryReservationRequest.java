package com.inventory.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryReservationRequest(
        @NotBlank String skuCode,
        @Min(1) Integer quantity
) {
}
