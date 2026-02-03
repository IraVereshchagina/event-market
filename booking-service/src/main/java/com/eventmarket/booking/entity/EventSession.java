package com.eventmarket.booking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "event_sessions")
@Data
public class EventSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer soldCount;

    @Column(nullable = false)
    private BigDecimal price;

    public boolean hasSeats(int count) {
        return (capacity - soldCount) >= count;
    }
}