package com.krish.ticket_booking.exception;

public class SeatUnavailableException extends RuntimeException {
    /**
     * Constructs a new SeatNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public SeatUnavailableException(String message) {
        super(message);
    }
}
