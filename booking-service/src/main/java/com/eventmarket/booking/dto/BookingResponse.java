package com.eventmarket.booking.dto;

import com.eventmarket.booking.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long ticketId;
    private TicketStatus status;
    private String message;
}