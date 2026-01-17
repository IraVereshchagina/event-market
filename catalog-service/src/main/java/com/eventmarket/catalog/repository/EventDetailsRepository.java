package com.eventmarket.catalog.repository;

import com.eventmarket.catalog.entity.EventDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface EventDetailsRepository extends MongoRepository<EventDetails, String> {
    Optional<EventDetails> findByEventId(Long eventId);
}