package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ScreenRegisterRequest(
        @NotBlank String name,
        @NotNull int totalSeats
) {
}
