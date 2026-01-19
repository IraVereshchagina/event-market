package com.eventmarket.booking.repository;

import com.eventmarket.booking.entity.Ticket;
import com.eventmarket.booking.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByStatusAndExpiresAtBefore(TicketStatus status, LocalDateTime now);
}