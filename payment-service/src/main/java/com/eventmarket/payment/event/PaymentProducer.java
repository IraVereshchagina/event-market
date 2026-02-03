package com.eventmarket.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payment.kafka.topic.payment-succeeded}")
    private String paymentSucceededTopic;

    @Value("${payment.kafka.topic.payment-failed}")
    private String paymentFailedTopic;

    public void sendPaymentSucceeded(Long ticketId) {
        log.info("Sending PaymentSucceededEvent for ticket: {}", ticketId);
        kafkaTemplate.send(paymentSucceededTopic, ticketId.toString(), new PaymentSucceededEvent(ticketId));
    }

    public void sendPaymentFailed(Long ticketId, String reason) {
        log.info("Sending PaymentFailedEvent for ticket: {}", ticketId);
        kafkaTemplate.send(paymentFailedTopic, ticketId.toString(), new PaymentFailedEvent(ticketId, reason));
    }
}