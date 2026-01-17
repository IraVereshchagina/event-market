package com.eventmarket.catalog;

import com.eventmarket.catalog.entity.Event;
import com.eventmarket.catalog.entity.EventDetails;
import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.repository.EventDetailsRepository;
import com.eventmarket.catalog.repository.EventRepository;
import com.eventmarket.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CatalogCacheTest extends AbstractCatalogIntegrationTest {

    @Autowired
    private CatalogService catalogService;

    @SpyBean
    private EventRepository eventRepository;

    @Autowired
    private EventDetailsRepository eventDetailsRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        eventDetailsRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void shouldCacheEventAfterFirstCall() {
        Event event = new Event();
        event.setTitle("Rock Concert");
        event.setCategory("CONCERT");
        event.setDate(LocalDateTime.now().plusDays(10));
        event.setPrice(BigDecimal.valueOf(5000));
        eventRepository.save(event);
        Long eventId = event.getId();

        EventDetails details = new EventDetails();
        details.setEventId(eventId);
        details.setDescription("Live music and beer");
        eventDetailsRepository.save(details);

        FullEventDto firstCall = catalogService.getEventById(eventId);

        assertThat(firstCall.getTitle()).isEqualTo("Rock Concert");
        assertThat(firstCall.getDescription()).isEqualTo("Live music and beer");

        verify(eventRepository, times(1)).findById(eventId);

        FullEventDto secondCall = catalogService.getEventById(eventId);

        assertThat(secondCall).isNotNull();

        verify(eventRepository, times(1)).findById(eventId);
    }
}