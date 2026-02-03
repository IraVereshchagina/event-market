package com.eventmarket.booking.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${booking.kafka.topic.ticket-booked}")
    private String ticketBookedTopic;

    public void sendTicketBookedEvent(TicketBookedEvent event) {
        log.info("Sending TicketBookedEvent to Kafka: {}", event);
        kafkaTemplate.send(ticketBookedTopic, event.getTicketId().toString(), event);
    }
}