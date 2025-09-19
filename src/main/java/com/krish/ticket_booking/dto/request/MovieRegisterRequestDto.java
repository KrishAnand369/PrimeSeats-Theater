package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MovieRegisterRequestDto(
        @NotBlank String title,
        @NotBlank String genre,
        @NotBlank String language,
        @NotBlank String posterUrl,

        @NotNull(message = "Price is Required")
        @PositiveOrZero(message = "Duration must be zero or greater")
        int duration
) {
}
