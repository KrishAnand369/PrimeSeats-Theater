package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReserveRequest(
        @NotNull UUID showId,
        @NotEmpty List<UUID> seatIds,     // up to 10
        @Email String guestEmail
) {
}

