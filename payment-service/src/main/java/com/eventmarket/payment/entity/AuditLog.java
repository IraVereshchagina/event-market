package com.eventmarket.payment.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Table("payment_audit_logs")
public class AuditLog {
    @Id
    private Long id;
    private Long paymentId;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;
}