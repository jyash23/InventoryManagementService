package com.inventory.inventory_service.config;

import com.inventory.inventory_service.grpc.InventoryGrpcServer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GrpcServerConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(
            @Value("${grpc.server.port}") int grpcPort,
            InventoryGrpcServer inventoryGrpcServer
    ) throws IOException {
        Server server = NettyServerBuilder.forPort(grpcPort)
                .addService(inventoryGrpcServer)
                .build();
        log.info("Starting inventory gRPC server on port {}", grpcPort);
        return server;
    }
}
