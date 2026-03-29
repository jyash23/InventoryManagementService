package com.order.order_service.service;

import com.order.order_service.dto.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-placed}")
    private String orderPlacedTopic;

    public void publish(OrderPlacedEvent event) {
        kafkaTemplate.send(orderPlacedTopic, event.orderNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order placed event for order {}", event.orderNumber(), ex);
                        return;
                    }
                    log.info("Published order placed event for order {} to topic {}", event.orderNumber(), orderPlacedTopic);
                });
    }
}
