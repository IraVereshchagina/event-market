package com.eventmarket.booking;

import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.dto.BookingRequest;
import com.eventmarket.booking.dto.BookingResponse;
import com.eventmarket.booking.repository.EventSessionRepository;
import com.eventmarket.booking.repository.TicketRepository;
import com.eventmarket.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingConcurrencyTest extends AbstractBookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventSessionRepository eventSessionRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        eventSessionRepository.deleteAll();
    }

    @Test
    void shouldNotOversellTickets_WhenConcurrentRequests() throws InterruptedException {
        EventSession session = new EventSession();
        session.setEventId(1L);
        session.setCapacity(5);
        session.setSoldCount(0);
        session.setPrice(new BigDecimal("1000.00"));
        eventSessionRepository.save(session);
        Long sessionId = session.getId();

        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulBookings = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = 1000 + i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    BookingRequest request = new BookingRequest();
                    request.setUserId(userId);
                    request.setEventSessionId(sessionId);

                    BookingResponse response = bookingService.bookTicket(request);

                    if (response.getTicketId() != null) {
                        successfulBookings.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        long actualTickets = ticketRepository.count();
        assertEquals(5, actualTickets, "Must be exactly 5 tickets created");

        EventSession updatedSession = eventSessionRepository.findById(sessionId).orElseThrow();
        assertEquals(5, updatedSession.getSoldCount(), "Session sold count must match capacity");

        System.out.println("Test passed! Successful bookings: " + successfulBookings.get());
    }
}