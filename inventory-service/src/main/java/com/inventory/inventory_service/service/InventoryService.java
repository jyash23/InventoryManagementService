package com.inventory.inventory_service.service;


import com.inventory.inventory_service.dto.InventoryResponse;
import com.inventory.inventory_service.dto.InventoryPageResponse;
import com.inventory.inventory_service.dto.InventoryRequest;
import com.inventory.inventory_service.dto.InventoryReservationRequest;
import com.inventory.inventory_service.dto.InventoryReservationResponse;
import com.inventory.inventory_service.model.Inventory;
import com.inventory.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final int MAX_PAGE_SIZE = 10;

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()).toList();
    }

    @Transactional(readOnly = true)
    public InventoryPageResponse getInventory(int page, int size) {
        int validatedPage = Math.max(page, 0);
        int validatedSize = size <= 0 ? MAX_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        Page<Inventory> inventoryPage = inventoryRepository.findAll(
                PageRequest.of(validatedPage, validatedSize, Sort.by(Sort.Direction.ASC, "id"))
        );

        return InventoryPageResponse.builder()
                .content(inventoryPage.getContent().stream()
                        .map(inventory -> InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build())
                        .toList())
                .pageNumber(inventoryPage.getNumber())
                .pageSize(inventoryPage.getSize())
                .totalElements(inventoryPage.getTotalElements())
                .totalPages(inventoryPage.getTotalPages())
                .last(inventoryPage.isLast())
                .build();
    }

    @Transactional
    public InventoryResponse upsertInventory(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findBySkuCode(inventoryRequest.skuCode())
                .orElseGet(Inventory::new);

        inventory.setSkuCode(inventoryRequest.skuCode());
        inventory.setQuantity(inventoryRequest.quantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .skuCode(savedInventory.getSkuCode())
                .isInStock(savedInventory.getQuantity() > 0)
                .build();
    }

    @Transactional
    public InventoryReservationResponse reserveInventory(List<InventoryReservationRequest> reservationRequests) {
        List<String> skuCodes = reservationRequests.stream()
                .map(InventoryReservationRequest::skuCode)
                .toList();

        Map<String, Inventory> inventoryMap = inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .collect(Collectors.toMap(Inventory::getSkuCode, Function.identity()));

        for (InventoryReservationRequest request : reservationRequests) {
            Inventory inventory = inventoryMap.get(request.skuCode());

            if (inventory == null) {
                return new InventoryReservationResponse(false, "Inventory item not found for skuCode: " + request.skuCode());
            }

            if (inventory.getQuantity() < request.quantity()) {
                return new InventoryReservationResponse(false, "Insufficient quantity for skuCode: " + request.skuCode());
            }
        }

        for (InventoryReservationRequest request : reservationRequests) {
            Inventory inventory = inventoryMap.get(request.skuCode());
            inventory.setQuantity(inventory.getQuantity() - request.quantity());
        }

        inventoryRepository.saveAll(inventoryMap.values());
        return new InventoryReservationResponse(true, "Inventory reserved successfully");

    }
}
