package com.eventmarket.payment.repository;

import com.eventmarket.payment.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByTicketId(Long ticketId);

    Optional<Payment> findByTransactionId(String transactionId);
}