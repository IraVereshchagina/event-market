package com.eventmarket.booking.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Long id) {
        super("Event Session with id " + id + " not found");
    }
}