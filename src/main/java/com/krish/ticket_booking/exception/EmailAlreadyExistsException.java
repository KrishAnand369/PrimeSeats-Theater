package com.krish.ticket_booking.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new EmailAlreadyExistsException with the specified detail message.
     * @param message The detail message.
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
