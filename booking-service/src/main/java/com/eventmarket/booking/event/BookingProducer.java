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

    @Value("${booking.kafka.topic.seats-updated}")
    private String seatsUpdatedTopic;

    public void sendTicketBookedEvent(TicketBookedEvent event) {
        log.info("Sending TicketBookedEvent to Kafka: {}", event);
        kafkaTemplate.send(ticketBookedTopic, event.getTicketId().toString(), event);
    }

    public void sendSeatsUpdatedEvent(SeatsUpdatedEvent event) {
        log.info("Sending SeatsUpdatedEvent for Event {} Session {}. Available: {}",
                event.getEventId(), event.getSessionId(), event.getAvailableSeats());

        kafkaTemplate.send(seatsUpdatedTopic, event.getEventId().toString(), event);
    }
}