package com.eventmarket.booking.service;

import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.entity.Ticket;
import com.eventmarket.booking.entity.TicketStatus;
import com.eventmarket.booking.dto.BookingRequest;
import com.eventmarket.booking.dto.BookingResponse;
import com.eventmarket.booking.event.BookingProducer;
import com.eventmarket.booking.exception.BookingAccessDeniedException;
import com.eventmarket.booking.repository.EventSessionRepository;
import com.eventmarket.booking.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private EventSessionRepository eventSessionRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private BookingProducer bookingProducer;

    @Mock
    private RLock rLock;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void bookTicket_Success() throws InterruptedException {
        Long userId = 101L;
        Long sessionId = 1L;
        BookingRequest request = new BookingRequest();
        request.setUserId(userId);
        request.setEventSessionId(sessionId);

        EventSession session = new EventSession();
        session.setId(sessionId);
        session.setCapacity(10);
        session.setSoldCount(0);
        session.setPrice(new BigDecimal("1000.00"));

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });

        when(eventSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId(777L);
            return t;
        });

        BookingResponse response = bookingService.bookTicket(request);

        assertNotNull(response.getTicketId());
        assertEquals(TicketStatus.NEW, response.getStatus());
        assertEquals("Booking successful", response.getMessage());

        assertEquals(1, session.getSoldCount());
        verify(eventSessionRepository).save(session);
        verify(rLock).unlock();
        verify(bookingProducer).sendTicketBookedEvent(any());
    }

    @Test
    void bookTicket_NoSeats() throws InterruptedException {
        BookingRequest request = new BookingRequest();
        request.setEventSessionId(1L);

        EventSession session = new EventSession();
        session.setId(1L);
        session.setCapacity(5);
        session.setSoldCount(5);

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });
        when(eventSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        BookingResponse response = bookingService.bookTicket(request);

        assertEquals(TicketStatus.CANCELLED, response.getStatus());
        assertEquals("No seats available", response.getMessage());

        verify(ticketRepository, never()).save(any());
        verify(rLock).unlock();
    }

    @Test
    void bookTicket_LockAcquisitionFailed() throws InterruptedException {
        BookingRequest request = new BookingRequest();
        request.setEventSessionId(1L);

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        BookingResponse response = bookingService.bookTicket(request);

        assertEquals(TicketStatus.CANCELLED, response.getStatus());
        assertEquals("Server is busy, please try again", response.getMessage());

        verify(eventSessionRepository, never()).findById(any());
    }

    @Test
    void cancelTicketByUser_Success() {
        Long ticketId = 10L;
        Long userId = 101L;
        Long sessionId = 5L;

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .userId(userId)
                .eventSessionId(sessionId)
                .status(TicketStatus.NEW)
                .build();

        EventSession session = new EventSession();
        session.setId(sessionId);
        session.setCapacity(10);
        session.setSoldCount(5);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(eventSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        bookingService.cancelTicketByUser(ticketId, userId);

        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
        assertEquals(4, session.getSoldCount());
        verify(ticketRepository).save(ticket);
        verify(eventSessionRepository).save(session);
    }

    @Test
    void cancelTicketByUser_AccessDenied() {
        Long ticketId = 10L;
        Long ownerId = 101L;
        Long hackerId = 999L;

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .userId(ownerId)
                .status(TicketStatus.NEW)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(BookingAccessDeniedException.class, () ->
                bookingService.cancelTicketByUser(ticketId, hackerId)
        );

        verify(ticketRepository, never()).save(any());
    }
}