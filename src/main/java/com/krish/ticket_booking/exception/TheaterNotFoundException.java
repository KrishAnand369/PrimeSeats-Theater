package com.krish.ticket_booking.exception;

public class TheaterNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public TheaterNotFoundException(String message) {
        super(message);
    }
}
