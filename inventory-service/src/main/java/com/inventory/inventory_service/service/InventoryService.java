package com.inventory.inventory_service.service;


import com.inventory.inventory_service.dto.InventoryResponse;
import com.inventory.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        // In a real application, this would check the database or an external service
        // For simplicity, we'll assume all products are in stock
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                                .build()).toList();

    }
}
