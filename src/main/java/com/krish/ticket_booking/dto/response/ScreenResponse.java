package com.krish.ticket_booking.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ScreenResponse(
        String name,
        int totalSeats,
        UUID theaterId
) {
}
