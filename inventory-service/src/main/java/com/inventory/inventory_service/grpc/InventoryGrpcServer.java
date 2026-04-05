package com.inventory.inventory_service.grpc;

import com.inventory.inventory_service.dto.InventoryReservationRequest;
import com.inventory.inventory_service.dto.InventoryReservationResponse;
import com.inventory.inventory_service.service.InventoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryGrpcServiceGrpc.InventoryGrpcServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public void reserveInventory(
            ReserveInventoryGrpcRequest request,
            StreamObserver<ReserveInventoryGrpcResponse> responseObserver
    ) {
        List<InventoryReservationRequest> reservationRequests = request.getItemsList().stream()
                .map(item -> new InventoryReservationRequest(item.getSkuCode(), item.getQuantity()))
                .toList();

        InventoryReservationResponse response = inventoryService.reserveInventory(reservationRequests);

        ReserveInventoryGrpcResponse grpcResponse = ReserveInventoryGrpcResponse.newBuilder()
                .setReserved(response.reserved())
                .setMessage(response.message())
                .build();

        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }
}
