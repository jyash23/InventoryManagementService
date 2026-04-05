package com.order.order_service.service;

import com.inventory.inventory_service.grpc.InventoryGrpcServiceGrpc;
import com.inventory.inventory_service.grpc.ReserveInventoryGrpcRequest;
import com.inventory.inventory_service.grpc.ReserveInventoryGrpcResponse;
import com.inventory.inventory_service.grpc.ReserveInventoryItem;
import com.order.order_service.dto.InventoryReservationRequest;
import com.order.order_service.dto.InventoryReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryGrpcClient {

    private final InventoryGrpcServiceGrpc.InventoryGrpcServiceBlockingStub inventoryGrpcServiceBlockingStub;

    public InventoryReservationResponse reserveInventory(List<InventoryReservationRequest> reservationRequests) {
        ReserveInventoryGrpcRequest request = ReserveInventoryGrpcRequest.newBuilder()
                .addAllItems(reservationRequests.stream()
                        .map(item -> ReserveInventoryItem.newBuilder()
                                .setSkuCode(item.skuCode())
                                .setQuantity(item.quantity())
                                .build())
                        .toList())
                .build();

        ReserveInventoryGrpcResponse response = inventoryGrpcServiceBlockingStub.reserveInventory(request);
        return new InventoryReservationResponse(response.getReserved(), response.getMessage());
    }
}
