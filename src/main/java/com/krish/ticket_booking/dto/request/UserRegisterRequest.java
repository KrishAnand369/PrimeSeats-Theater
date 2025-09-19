package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password
) {}
