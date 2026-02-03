package com.eventmarket.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${notification.kafka.topic.seats-updated}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleSeatsUpdated(SeatsUpdatedEvent event) {
        log.info("Received update for Event {}: {} seats left", event.getEventId(), event.getAvailableSeats());

        String destination = "/topic/events/" + event.getEventId();

        messagingTemplate.convertAndSend(destination, event);
    }
}