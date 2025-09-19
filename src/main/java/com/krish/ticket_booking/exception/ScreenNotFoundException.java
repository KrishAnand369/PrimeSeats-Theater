package com.krish.ticket_booking.exception;

public class ScreenNotFoundException extends RuntimeException {

    /**
     * Constructs a new ScreenNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public ScreenNotFoundException(String message) {
        super(message);
    }
}
