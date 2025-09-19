package com.krish.ticket_booking.exception;

public class InternalException extends RuntimeException {

    /**
     * Constructs a new InternalException with the specified detail message.
     * @param message The detail message.
     */
    public InternalException(String message) {
        super(message);
    }
}
