package com.eventmarket.catalog.controller;

import com.eventmarket.catalog.dto.FullEventDto;
import com.eventmarket.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final CatalogService catalogService;

    @GetMapping("/{id}")
    public FullEventDto getEvent(@PathVariable Long id) {
        return catalogService.getEventById(id);
    }
}