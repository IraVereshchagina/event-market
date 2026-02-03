package com.eventmarket.booking.controller;

import com.eventmarket.booking.dto.BookingRequest;
import com.eventmarket.booking.dto.BookingResponse;
import com.eventmarket.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse bookTicket(@RequestBody BookingRequest request) {
        return bookingService.bookTicket(request);
    }

    @PostMapping("/{ticketId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long ticketId,
            @RequestParam Long userId) {
        bookingService.cancelTicketByUser(ticketId, userId);
        return ResponseEntity.noContent().build();
    }
}