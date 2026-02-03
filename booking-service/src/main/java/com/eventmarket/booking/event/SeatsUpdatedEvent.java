package com.eventmarket.booking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatsUpdatedEvent {
    private Long eventId;
    private Long sessionId;
    private int availableSeats;
}