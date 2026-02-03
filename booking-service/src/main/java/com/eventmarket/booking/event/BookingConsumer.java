package com.eventmarket.booking.event;

import com.eventmarket.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingConsumer {

    private final BookingService bookingService;

    @KafkaListener(topics = "${booking.kafka.topic.payment-succeeded}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Payment succeeded for ticket {}", event.getTicketId());
        bookingService.markTicketAsPaid(event.getTicketId());
    }

    @KafkaListener(topics = "${booking.kafka.topic.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Payment failed for ticket {}: {}", event.getTicketId(), event.getReason());
        bookingService.cancelTicket(event.getTicketId());
    }
}