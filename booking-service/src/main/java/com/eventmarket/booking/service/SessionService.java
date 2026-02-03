package com.eventmarket.booking.service;

import com.eventmarket.booking.dto.SessionCreateRequest;
import com.eventmarket.booking.entity.EventSession;
import com.eventmarket.booking.repository.EventSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final EventSessionRepository eventSessionRepository;

    @Transactional
    public Long createSession (SessionCreateRequest request){
        log.info("Creating sales session for Event ID: {}", request.getEventId());

        EventSession session = new EventSession();
        session.setEventId(request.getEventId());
        session.setCapacity(request.getCapacity());
        session.setPrice(request.getPrice());
        session.setSoldCount(0);

        EventSession savedSession = eventSessionRepository.save(session);
        log.info("Session created with ID: {}", savedSession.getId());

        return savedSession.getId();
    }
}
