package com.eventmarket.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private Long id;

    private Long ticketId;
    private Long userId;

    private BigDecimal amount;
    private BigDecimal platformFee;
    private BigDecimal organizerPayout;

    private String status;
    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment create(Long ticketId, Long userId, BigDecimal amount, BigDecimal feeRate) {
        BigDecimal fee = amount.multiply(feeRate);
        BigDecimal payout = amount.subtract(fee);

        return Payment.builder()
                .ticketId(ticketId)
                .userId(userId)
                .amount(amount)
                .platformFee(fee)
                .organizerPayout(payout)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }
}