package com.eventmarket.catalog.db.migration;

import com.eventmarket.catalog.entity.EventDetails;
import com.eventmarket.catalog.repository.EventDetailsRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ChangeUnit(id = "001-init-event-details", order = "001", author = "irina")
@RequiredArgsConstructor
public class DatabaseChangeLog {

    private final EventDetailsRepository repository;

    @Execution
    public void execute() {
        repository.save(createDetails(1L, "Biggest Java Conference", List.of("Josh Long", "Venkat Subramaniam")));
        repository.save(createDetails(2L, "Rock Legends Live", List.of("Metallica", "AC/DC")));
        repository.save(createDetails(3L, "DevOps Practices", List.of("Patrick Debois")));
        repository.save(createDetails(4L, "Smooth Jazz Night", List.of("Louis Armstrong")));
        repository.save(createDetails(5L, "Online Tech Talk", List.of("Martin Fowler")));
    }

    @RollbackExecution
    public void rollback() {
        repository.deleteAll();
    }

    private EventDetails createDetails(Long eventId, String description, List<String> speakers) {
        EventDetails details = new EventDetails();
        details.setEventId(eventId);
        details.setDescription(description);
        details.setSpeakers(speakers);
        return details;
    }
}