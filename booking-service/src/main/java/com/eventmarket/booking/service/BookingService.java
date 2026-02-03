package com.eventmarket.booking.service;

import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.entity.Ticket;
import com.eventmarket.booking.entity.TicketStatus;
import com.eventmarket.booking.dto.BookingRequest;
import com.eventmarket.booking.dto.BookingResponse;
import com.eventmarket.booking.event.BookingProducer;
import com.eventmarket.booking.event.TicketBookedEvent;
import com.eventmarket.booking.exception.BookingAccessDeniedException;
import com.eventmarket.booking.exception.BookingLockException;
import com.eventmarket.booking.exception.SessionNotFoundException;
import com.eventmarket.booking.exception.TicketNotFoundException;
import com.eventmarket.booking.repository.EventSessionRepository;
import com.eventmarket.booking.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final EventSessionRepository eventSessionRepository;
    private final TicketRepository ticketRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;
    private final BookingProducer bookingProducer;

    public BookingResponse bookTicket(BookingRequest request) {
        String lockKey = "lock:session:" + request.getEventSessionId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!isLocked) {
                return new BookingResponse(null, TicketStatus.CANCELLED, "Server is busy, please try again");
            }

            return transactionTemplate.execute(status -> {
                EventSession session = eventSessionRepository.findById(request.getEventSessionId())
                        .orElseThrow(() -> new SessionNotFoundException(request.getEventSessionId()));

                if (session.getSoldCount() >= session.getCapacity()) {
                    return new BookingResponse(null, TicketStatus.CANCELLED, "No seats available");
                }

                session.setSoldCount(session.getSoldCount() + 1);
                eventSessionRepository.save(session);

                Ticket ticket = Ticket.createNew(request.getUserId(), session.getId());

                ticketRepository.save(ticket);

                log.info("Ticket booked: ID={}, User={}, Session={}", ticket.getId(), request.getUserId(), session.getId());

                TicketBookedEvent event = new TicketBookedEvent(
                        ticket.getId(),
                        ticket.getUserId(),
                        session.getPrice()
                );
                bookingProducer.sendTicketBookedEvent(event);

                return new BookingResponse(ticket.getId(), ticket.getStatus(), "Booking successful");
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BookingLockException("Interrupted while waiting for lock");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void markTicketAsPaid(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == TicketStatus.PAID) {
            log.info("Ticket {} is already PAID", ticketId);
            return;
        }

        ticket.setStatus(TicketStatus.PAID);
        ticketRepository.save(ticket);
        log.info("Ticket {} marked as PAID", ticketId);
    }

    @Transactional
    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() != TicketStatus.NEW) {
            return;
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        EventSession session = eventSessionRepository.findById(ticket.getEventSessionId())
                .orElseThrow(() -> new SessionNotFoundException(ticket.getEventSessionId()));

        if (session.getSoldCount() > 0) {
            session.setSoldCount(session.getSoldCount() - 1);
            eventSessionRepository.save(session);
            log.info("Ticket {} cancelled. Seat returned to Session {}", ticket.getId(), session.getId());
        }
    }

    @Transactional
    public void cancelTicketByUser(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (!ticket.getUserId().equals(userId)) {
            throw new BookingAccessDeniedException("You are not the owner of this ticket");
        }

        cancelTicket(ticketId);
    }
}