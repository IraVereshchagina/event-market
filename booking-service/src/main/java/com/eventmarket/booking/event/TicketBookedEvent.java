package com.eventmarket.booking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketBookedEvent {
    private Long ticketId;
    private Long userId;
    private BigDecimal price;
}