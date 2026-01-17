package com.eventmarket.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private String description;
    private List<String> speakers;
}