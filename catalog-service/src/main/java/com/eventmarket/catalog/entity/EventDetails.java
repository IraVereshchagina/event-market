package com.eventmarket.catalog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "event_details")
@Data
public class EventDetails {
    @Id
    private String id;

    private Long eventId;

    private String description;
    private String program;
    private List<String> speakers;
}