package com.inventory.inventory_service.controller;

import com.inventory.inventory_service.dto.InventoryResponse;
import com.inventory.inventory_service.dto.InventoryPageResponse;
import com.inventory.inventory_service.dto.InventoryRequest;
import com.inventory.inventory_service.dto.InventoryReservationRequest;
import com.inventory.inventory_service.dto.InventoryReservationResponse;
import com.inventory.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping(params = "skuCode")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public InventoryPageResponse getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return inventoryService.getInventory(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createOrUpdateInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {
        return inventoryService.upsertInventory(inventoryRequest);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    public InventoryReservationResponse reserveInventory(
            @Valid @RequestBody List<InventoryReservationRequest> reservationRequests
    ) {
        return inventoryService.reserveInventory(reservationRequests);
    }
}
