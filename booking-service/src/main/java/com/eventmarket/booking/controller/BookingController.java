package com.eventmarket.booking.controller;

import com.eventmarket.booking.dto.BookingRequest;
import com.eventmarket.booking.dto.BookingResponse;
import com.eventmarket.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse bookTicket(@RequestBody BookingRequest request) {
        return bookingService.bookTicket(request);
    }
}