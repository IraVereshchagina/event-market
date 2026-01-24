package com.eventmarket.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mock-stripe")
@Slf4j
@RequiredArgsConstructor
public class MockStripeController {

    @PostMapping("/pay")
    public Map<String, String> createPayment(@RequestBody Map<String, Object> request) {
        String transactionId = UUID.randomUUID().toString();
        log.info("MockStripe: Payment received. Amount: {}. Transaction ID: {}", request.get("amount"), transactionId);

        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                sendWebhook(transactionId, "SUCCESS");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return Map.of("id", transactionId, "status", "pending");
    }

    private void sendWebhook(String transactionId, String status) {
        log.info("MockStripe: Sending webhook for {}", transactionId);

        RestClient restClient = RestClient.create();

        try {
            restClient.post()
                    .uri("http://localhost:8084/api/v1/payments/webhook")
                    .body(Map.of(
                            "event", "payment_intent.succeeded",
                            "data", Map.of(
                                    "id", transactionId,
                                    "status", status
                            )
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("MockStripe: Failed to send webhook", e);
        }
    }
}