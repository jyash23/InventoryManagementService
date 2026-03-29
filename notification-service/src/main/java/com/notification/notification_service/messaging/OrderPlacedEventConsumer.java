package com.notification.notification_service.messaging;

import com.notification.notification_service.dto.OrderPlacedEvent;
import com.notification.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topic.order-placed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderPlacedEvent event) {
        log.info("Received order placed event for order {}", event.orderNumber());
        notificationService.generateNotification(event);
    }
}
