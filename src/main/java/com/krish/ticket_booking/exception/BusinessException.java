package com.krish.ticket_booking.exception;

public class BusinessException extends RuntimeException {

    /**
     * Constructs a new BusinessException with the specified detail message.
     * @param message The detail message.
     */
    public BusinessException(String message) {
        super(message);
    }
}
