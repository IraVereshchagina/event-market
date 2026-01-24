package com.eventmarket.payment;

import com.eventmarket.payment.entity.Payment;
import com.eventmarket.payment.dto.PaymentRequest;
import com.eventmarket.payment.repository.AuditLogRepository;
import com.eventmarket.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class PaymentFlowTest extends AbstractPaymentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void fullPaymentCycle_ShouldSucceed() {
        PaymentRequest request = new PaymentRequest();
        request.setTicketId(100L);
        request.setUserId(55L);
        request.setAmount(new BigDecimal("1000.00"));

        ResponseEntity<Payment> response = restTemplate.postForEntity(
                "/api/v1/payments",
                request,
                Payment.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("PENDING");

        Long paymentId = response.getBody().getId();

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    Payment updatedPayment = paymentRepository.findById(paymentId).orElseThrow();
                    assertThat(updatedPayment.getStatus()).isEqualTo("SUCCESS");
                });

        assertThat(auditLogRepository.count()).isGreaterThan(0);
    }
}