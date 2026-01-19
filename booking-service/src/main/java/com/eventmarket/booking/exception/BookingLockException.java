package com.eventmarket.booking.exception;

public class BookingLockException extends RuntimeException {
    public BookingLockException(String message) {
        super(message);
    }
}