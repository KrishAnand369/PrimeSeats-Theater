package com.krish.ticket_booking.exception;

public class SeatNotFoundException extends RuntimeException {

    /**
     * Constructs a new SeatNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public SeatNotFoundException(String message) {
        super(message);
    }
}
