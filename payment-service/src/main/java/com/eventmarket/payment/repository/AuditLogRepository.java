package com.eventmarket.payment.repository;

import com.eventmarket.payment.entity.AuditLog;
import org.springframework.data.repository.CrudRepository;

public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {
}