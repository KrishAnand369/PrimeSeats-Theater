package com.krish.ticket_booking.exception;

public class MovieNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public MovieNotFoundException(String message) {
        super(message);
    }
}
