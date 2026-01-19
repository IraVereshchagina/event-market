package com.eventmarket.booking.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long userId;
    private Long eventSessionId;
}