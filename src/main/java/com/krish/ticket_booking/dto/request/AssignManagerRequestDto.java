package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignManagerRequestDto(@NotNull  UUID userId,@NotNull UUID theaterId) {
}
