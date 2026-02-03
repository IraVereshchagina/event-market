package com.eventmarket.notification.service;

import com.eventmarket.notification.event.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @KafkaListener(topics = "${notification.kafka.topic.payment-succeeded}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendNotification(PaymentSucceededEvent event) {
        log.info("SENDING EMAIL: Ticket {} is successfully paid! Check your inbox.", event.getTicketId());
    }
}
