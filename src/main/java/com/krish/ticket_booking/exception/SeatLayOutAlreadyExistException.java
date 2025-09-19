package com.krish.ticket_booking.exception;

public class SeatLayOutAlreadyExistException extends RuntimeException {

    /**
     * Constructs a new SeatLayOutAlreadyExistException with the specified detail message.
     * @param message The detail message.
     */
    public SeatLayOutAlreadyExistException(String message) {
        super(message);
    }
}
