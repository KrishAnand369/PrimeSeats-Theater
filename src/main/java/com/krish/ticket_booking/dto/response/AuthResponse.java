package com.krish.ticket_booking.dto.response;

public record AuthResponse(
        String token,
        String role
) {}
