package com.eventmarket.payment.event;

import com.eventmarket.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${payment.kafka.topic.ticket-booked}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleTicketBooked(TicketBookedEvent event) {
        log.info("Received TicketBookedEvent: {}", event);

        try {
            paymentService.initiatePayment(event.getTicketId(), event.getUserId(), event.getPrice());
        } catch (Exception e) {
            log.error("Failed to initiate payment for ticket {}", event.getTicketId(), e);
        }
    }
}