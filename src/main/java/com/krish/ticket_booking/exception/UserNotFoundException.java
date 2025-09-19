package com.krish.ticket_booking.exception;

public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
