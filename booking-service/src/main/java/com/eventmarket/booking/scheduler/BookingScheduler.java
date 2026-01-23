package com.eventmarket.booking.scheduler;

import com.eventmarket.booking.entity.Ticket;
import com.eventmarket.booking.entity.TicketStatus;
import com.eventmarket.booking.repository.TicketRepository;
import com.eventmarket.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {

    private final TicketRepository ticketRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRateString = "${booking.scheduler.rate}")
    @Transactional
    public void cancelExpiredTickets() {
        List<Ticket> expiredTickets = ticketRepository.findAllByStatusAndExpiresAtBefore(
                TicketStatus.NEW,
                LocalDateTime.now()
        );

        if (!expiredTickets.isEmpty()) {
            log.info("Found {} expired tickets", expiredTickets.size());
            for (Ticket ticket : expiredTickets) {
                try {
                    bookingService.cancelTicket(ticket.getId());
                } catch (Exception e) {
                    log.error("Failed to cancel ticket {}", ticket.getId(), e);
                }
            }
        }
    }
}