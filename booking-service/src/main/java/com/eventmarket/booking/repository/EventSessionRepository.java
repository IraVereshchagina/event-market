package com.eventmarket.booking.repository;

import com.eventmarket.booking.entity.EventSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSessionRepository extends JpaRepository<EventSession, Long> {
}