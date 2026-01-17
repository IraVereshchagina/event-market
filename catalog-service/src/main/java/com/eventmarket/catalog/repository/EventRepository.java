package com.eventmarket.catalog.repository;

import com.eventmarket.catalog.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}