package com.eventmarket.catalog.service;

import com.eventmarket.catalog.entity.Event;
import com.eventmarket.catalog.entity.EventDetails;
import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.repository.EventDetailsRepository;
import com.eventmarket.catalog.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private EventDetailsRepository eventDetailsRepository;
    @InjectMocks private CatalogService catalogService;

    @Test
    void getEventById_ShouldCombineDataFromPostgresAndMongo() {
        Long eventId = 1L;

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Java Conference");
        event.setPrice(BigDecimal.valueOf(1000));

        EventDetails details = new EventDetails();
        details.setEventId(eventId);
        details.setDescription("Best conference ever");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventDetailsRepository.findByEventId(eventId)).thenReturn(Optional.of(details));

        FullEventDto result = catalogService.getEventById(eventId);

        assertEquals("Java Conference", result.getTitle());
        assertEquals("Best conference ever", result.getDescription());
    }
}