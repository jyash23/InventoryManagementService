package com.notification.notification_service.service;

import com.notification.notification_service.dto.OrderPlacedEvent;
import com.notification.notification_service.model.Notification;
import com.notification.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void generateNotification(OrderPlacedEvent event) {
        Notification notification = Notification.builder()
                .orderNumber(event.orderNumber())
                .message("Order " + event.orderNumber() + " has been placed successfully")
                .totalAmount(event.totalAmount())
                .createdAt(event.createdAt())
                .build();

        notificationRepository.save(notification);
        log.info("Notification generated for order {}", event.orderNumber());
    }

    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }
}
