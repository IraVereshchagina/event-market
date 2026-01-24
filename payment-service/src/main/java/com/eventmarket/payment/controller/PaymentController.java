package com.eventmarket.payment.controller;

import com.eventmarket.payment.dto.PaymentRequest;
import com.eventmarket.payment.entity.Payment;
import com.eventmarket.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment pay(@RequestBody PaymentRequest request) {
        return paymentService.initiatePayment(request.getTicketId(), request.getUserId(), request.getAmount());
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        paymentService.processWebhook(payload);
    }
}
