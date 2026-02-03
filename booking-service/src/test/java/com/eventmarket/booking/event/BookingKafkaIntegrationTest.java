package com.eventmarket.booking.event;

import com.eventmarket.booking.AbstractBookingIntegrationTest;
import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.entity.Ticket;
import com.eventmarket.booking.entity.TicketStatus;
import com.eventmarket.booking.repository.EventSessionRepository;
import com.eventmarket.booking.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class BookingKafkaIntegrationTest extends AbstractBookingIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventSessionRepository eventSessionRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${booking.kafka.topic.payment-succeeded}")
    private String paymentSucceededTopic;

    @BeforeEach
    void cleanUp() {
        ticketRepository.deleteAll();
        eventSessionRepository.deleteAll();
    }

    @Test
    void shouldChangeTicketStatusToPaid_WhenPaymentSucceededEventReceived() {
        EventSession session = new EventSession();
        session.setEventId(1L);
        session.setCapacity(10);
        session.setSoldCount(1);
        session.setPrice(new BigDecimal("1000.00"));
        eventSessionRepository.save(session);

        Ticket ticket = Ticket.builder()
                .userId(101L)
                .eventSessionId(session.getId())
                .status(TicketStatus.NEW)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        ticketRepository.save(ticket);

        Long ticketId = ticket.getId();

        PaymentSucceededEvent event = new PaymentSucceededEvent(ticketId);

        kafkaTemplate.send(paymentSucceededTopic, ticketId.toString(), event);

        await()
                .atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    Ticket updatedTicket = ticketRepository.findById(ticketId).orElseThrow();
                    assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.PAID);
                });
    }
}