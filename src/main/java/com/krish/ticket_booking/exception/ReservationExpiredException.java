package com.krish.ticket_booking.exception;

public class ReservationExpiredException extends RuntimeException {

    /**
     * Constructs a new ReservationExpiredException with the specified detail message.
     * @param message The detail message.
     */
    public ReservationExpiredException(String message) {
        super(message);
    }
}
