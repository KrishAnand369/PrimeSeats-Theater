package com.krish.ticket_booking.controller;

import com.krish.ticket_booking.dto.response.ErrorResponseDto;
import com.krish.ticket_booking.exception.EmailAlreadyExistsException;
import com.krish.ticket_booking.exception.InternalException;
import com.krish.ticket_booking.exception.MovieNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A global exception handler to centralize the handling of exceptions
 * across all controllers in the application.
 */
@RestControllerAdvice
@Hidden //to avoid confusions in the swagger documentation
public class GlobalExceptionHandler {


    /**
     * Handles EmailAlreadyExistsException and returns a ResponseEntity with a 409 Conflict status.
     * This handler is more specific and will be chosen by Spring when a user
     * tries to register with a duplicate email.
     * @param ex The EmailAlreadyExistsException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse DTO.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     *
     * @param ex The UserNotFoundException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse DTO.
     */

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param ex the MovieNotFoundException that was thrown
     * @return A ResponseEntity containing an ErrorResponse DTO
     */

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMovieNotFoundException(MovieNotFoundException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles RuntimeException and returns a ResponseEntity with a 400 Bad Request status.
     * This method acts as a general fallback for any unchecked exceptions that
     * don't have a more specific handler defined.
     * @param ex The RuntimeException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse DTO.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex) {
        // Create a custom DTO to return a structured error response
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponseDto> handleInternalException(InternalException ex) {

        // Create a custom DTO to return a structured error response
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
