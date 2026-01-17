package com.eventmarket.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateEventRequest {
    private String title;
    private String category;
    private LocalDateTime date;
    private String city;
    private BigDecimal price;
    private String description;
    private List<String> speakers;
}