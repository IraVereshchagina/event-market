package com.eventmarket.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventFilter {
    private String city;
    private String category;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}