package com.order.order_service.service;


import com.order.order_service.dto.InventoryResponse;
import com.order.order_service.dto.OrderItemEvent;
import com.order.order_service.dto.OrderPlacedEvent;
import com.order.order_service.dto.OrderLineItemsDto;
import com.order.order_service.dto.OrderRequest;
import com.order.order_service.exception.ProductOutOfStockException;
import com.order.order_service.model.Order;
import com.order.order_service.model.OrderLineItems;
import com.order.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final OrderEventPublisher orderEventPublisher;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList =orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodes=order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();
        InventoryResponse[] inventoryResponses=webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(res -> Boolean.TRUE.equals(res.getIsInStock()));


        if(allProductsInStock) {
            orderRepository.save(order);
            orderEventPublisher.publish(buildOrderPlacedEvent(order));
        }else {
            throw new ProductOutOfStockException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    private OrderPlacedEvent buildOrderPlacedEvent(Order order) {
        List<OrderItemEvent> items = order.getOrderLineItemsList().stream()
                .map(item -> new OrderItemEvent(
                        item.getSkuCode(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderPlacedEvent(
                order.getOrderNumber(),
                items,
                items.stream()
                        .map(OrderItemEvent::price)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add),
                Instant.now()
        );
    }
}
