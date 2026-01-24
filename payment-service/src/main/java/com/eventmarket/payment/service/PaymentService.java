package com.eventmarket.payment.service;

import com.eventmarket.payment.entity.AuditLog;
import com.eventmarket.payment.entity.Payment;
import com.eventmarket.payment.repository.AuditLogRepository;
import com.eventmarket.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;

    @Value("${payment.fee-percentage}")
    private BigDecimal feePercentage;

    @Transactional
    public Payment initiatePayment(Long ticketId, Long userId, BigDecimal amount) {
        Payment payment = Payment.create(ticketId, userId, amount, feePercentage);
        payment = paymentRepository.save(payment);

        String transactionId = callPaymentGateway(amount);

        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        } else {
            payment.setStatus("FAILED");
            throw new RuntimeException("Payment gateway failed");
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public void processWebhook(Map<String, Object> payload) {
        WebhookData data = parseWebhook(payload);

        Payment payment = paymentRepository.findByTransactionId(data.txId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(data.status());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        logAudit(payment.getId(), payload.toString());
    }

    private String callPaymentGateway(BigDecimal amount) {
        try {
            RestClient restClient = RestClient.create();
            Map response = restClient.post()
                    .uri("http://localhost:8084/mock-stripe/pay")
                    .body(Map.of("amount", amount, "currency", "USD"))
                    .retrieve()
                    .body(Map.class);
            return (String) response.get("id");
        } catch (Exception e) {
            log.error("Gateway error", e);
            return null;
        }
    }

    private void logAudit(Long paymentId, String payload) {
        AuditLog logEntry = new AuditLog();
        logEntry.setPaymentId(paymentId);
        logEntry.setEventType("WEBHOOK_RECEIVED");
        logEntry.setPayload(payload);
        logEntry.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(logEntry);
    }

    private record WebhookData(String txId, String status) {}

    private WebhookData parseWebhook(Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        return new WebhookData((String) data.get("id"), (String) data.get("status"));
    }
}