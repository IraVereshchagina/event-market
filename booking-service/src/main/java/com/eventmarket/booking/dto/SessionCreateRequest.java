package com.eventmarket.booking.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SessionCreateRequest {
    private Long eventId;
    private int capacity;
    private BigDecimal price;
}