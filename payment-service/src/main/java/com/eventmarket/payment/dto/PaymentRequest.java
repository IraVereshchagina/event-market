package com.eventmarket.payment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long ticketId;
    private Long userId;
    private BigDecimal amount;
}