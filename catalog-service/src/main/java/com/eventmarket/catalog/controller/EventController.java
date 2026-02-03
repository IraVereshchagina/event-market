package com.eventmarket.catalog.controller;

import com.eventmarket.catalog.dto.CreateEventRequest;
import com.eventmarket.catalog.dto.EventFilter;
import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.dto.UpdateEventRequest;
import com.eventmarket.catalog.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final CatalogService catalogService;

    @GetMapping
    public List<FullEventDto> getEvents(
            @ModelAttribute EventFilter filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return catalogService.getEvents(filter, pageable);
    }

    @GetMapping("/{id}")
    public FullEventDto getEvent(@PathVariable Long id) {
        return catalogService.getEventById(id);
    }

    @PostMapping
    public FullEventDto createEvent(@Valid @RequestBody CreateEventRequest request) {
        return catalogService.createEvent(request);
    }

    @PutMapping("/{id}")
    public FullEventDto updateEvent(@PathVariable Long id, @RequestBody UpdateEventRequest request) {
        return catalogService.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        catalogService.deleteEvent(id);
    }
}