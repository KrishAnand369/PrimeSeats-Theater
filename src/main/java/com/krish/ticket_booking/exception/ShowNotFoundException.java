package com.krish.ticket_booking.exception;

public class ShowNotFoundException extends RuntimeException {

    /**
     * Constructs a new ShowNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public ShowNotFoundException(String message) {
        super(message);
    }
}
