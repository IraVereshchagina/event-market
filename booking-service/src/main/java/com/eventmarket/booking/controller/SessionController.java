package com.eventmarket.booking.controller;

import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.dto.SessionCreateRequest;
import com.eventmarket.booking.repository.EventSessionRepository;
import com.eventmarket.booking.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createSession(@RequestBody SessionCreateRequest request) {
        return sessionService.createSession(request);
    }
}