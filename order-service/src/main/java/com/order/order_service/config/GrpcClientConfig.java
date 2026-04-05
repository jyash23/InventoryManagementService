package com.order.order_service.config;

import com.inventory.inventory_service.grpc.InventoryGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel inventoryManagedChannel(
            @Value("${inventory.grpc.host}") String host,
            @Value("${inventory.grpc.port}") int port
    ) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public InventoryGrpcServiceGrpc.InventoryGrpcServiceBlockingStub inventoryGrpcServiceBlockingStub(
            ManagedChannel inventoryManagedChannel
    ) {
        return InventoryGrpcServiceGrpc.newBlockingStub(inventoryManagedChannel);
    }
}
