package com.eventmarket.catalog.specification;

import com.eventmarket.catalog.entity.Event;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventSpecification {

    private EventSpecification() {}

    public static Specification<Event> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(root.get("city"), city);
    }

    public static Specification<Event> hasCategory(String category) {
        return (root, query, cb) ->
                category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Event> dateGreaterThan(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? null : cb.greaterThanOrEqualTo(root.get("date"), date);
    }

    public static Specification<Event> dateLessThan(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("date"), date);
    }

    public static Specification<Event> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Event> priceLessThan(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}