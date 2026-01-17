package com.eventmarket.catalog.service;

import com.eventmarket.catalog.entity.Event;
import com.eventmarket.catalog.entity.EventDetails;
import com.eventmarket.catalog.dto.CreateEventRequest;
import com.eventmarket.catalog.dto.EventFilter;
import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.dto.UpdateEventRequest;
import com.eventmarket.catalog.exception.EventNotFoundException;
import com.eventmarket.catalog.repository.EventDetailsRepository;
import com.eventmarket.catalog.repository.EventRepository;
import com.eventmarket.catalog.specification.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final EventRepository eventRepository;
    private final EventDetailsRepository eventDetailsRepository;

    @Cacheable(value = "events", key = "#id")
    public FullEventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        EventDetails details = eventDetailsRepository.findByEventId(id)
                .orElse(new EventDetails());

        return mapToDto(event, details);
    }

    public List<FullEventDto> getEvents(EventFilter filter, Pageable pageable) {
        Specification<Event> spec = Specification.where(EventSpecification.hasCity(filter.getCity()))
                .and(EventSpecification.hasCategory(filter.getCategory()))
                .and(EventSpecification.dateGreaterThan(filter.getDateFrom()))
                .and(EventSpecification.dateLessThan(filter.getDateTo()))
                .and(EventSpecification.priceGreaterThan(filter.getMinPrice()))
                .and(EventSpecification.priceLessThan(filter.getMaxPrice()));

        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.getContent().stream()
                .map(event -> {
                    EventDetails details = eventDetailsRepository.findByEventId(event.getId())
                            .orElse(new EventDetails());
                    return mapToDto(event, details);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FullEventDto createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setCategory(request.getCategory());
        event.setDate(request.getDate());
        event.setCity(request.getCity());
        event.setPrice(request.getPrice());

        event = eventRepository.save(event);

        EventDetails details = new EventDetails();
        details.setEventId(event.getId());
        details.setDescription(request.getDescription());
        details.setSpeakers(request.getSpeakers());

        eventDetailsRepository.save(details);

        return mapToDto(event, details);
    }

    @Transactional
    @CacheEvict(value = "events", key = "#id")
    public FullEventDto updateEvent(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getDate() != null) event.setDate(request.getDate());
        if (request.getCity() != null) event.setCity(request.getCity());
        if (request.getPrice() != null) event.setPrice(request.getPrice());

        eventRepository.save(event);

        EventDetails details = eventDetailsRepository.findByEventId(id)
                .orElse(new EventDetails());
        details.setEventId(id);

        if (request.getDescription() != null) details.setDescription(request.getDescription());
        if (request.getSpeakers() != null) details.setSpeakers(request.getSpeakers());

        eventDetailsRepository.save(details);

        return mapToDto(event, details);
    }

    @Transactional
    @CacheEvict(value = "events", key = "#id")
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);

        eventDetailsRepository.findByEventId(id)
                .ifPresent(eventDetailsRepository::delete);
    }

    private FullEventDto mapToDto(Event event, EventDetails details) {
        return FullEventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory())
                .date(event.getDate())
                .city(event.getCity())
                .price(event.getPrice())
                .description(details != null ? details.getDescription() : null)
                .speakers(details != null ? details.getSpeakers() : null)
                .build();
    }
}