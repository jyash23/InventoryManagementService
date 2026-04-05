package com.order.order_service.service;

import com.order.order_service.dto.InventoryReservationRequest;
import com.order.order_service.dto.InventoryReservationResponse;
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

import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final OrderEventPublisher orderEventPublisher;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList =orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<InventoryReservationRequest> reservationRequests = order.getOrderLineItemsList().stream()
                .map(item -> new InventoryReservationRequest(item.getSkuCode(), item.getQuantity()))
                .toList();

        InventoryReservationResponse inventoryReservationResponse =
                inventoryGrpcClient.reserveInventory(reservationRequests);

        if(inventoryReservationResponse != null && inventoryReservationResponse.reserved()) {
            orderRepository.save(order);
            orderEventPublisher.publish(buildOrderPlacedEvent(order));
        }else {
            throw new ProductOutOfStockException(
                    inventoryReservationResponse != null
                            ? inventoryReservationResponse.message()
                            : "Product is not in stock, please try again later"
            );
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
