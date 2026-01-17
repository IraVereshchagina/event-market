package com.eventmarket.catalog.service;

import com.eventmarket.catalog.entity.Event;
import com.eventmarket.catalog.entity.EventDetails;
import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.repository.EventDetailsRepository;
import com.eventmarket.catalog.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final EventRepository eventRepository;
    private final EventDetailsRepository eventDetailsRepository;

    @Cacheable(value = "events", key = "#id")
    public FullEventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventDetails details = eventDetailsRepository.findByEventId(id)
                .orElse(new EventDetails());

        return FullEventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory())
                .date(event.getDate())
                .city(event.getCity())
                .price(event.getPrice())
                .description(details.getDescription())
                .speakers(details.getSpeakers())
                .build();
    }
}