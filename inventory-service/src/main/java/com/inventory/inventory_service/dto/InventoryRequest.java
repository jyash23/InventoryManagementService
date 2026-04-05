package com.inventory.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryRequest(
        @NotBlank String skuCode,
        @Min(0) Integer quantity
) {
}
