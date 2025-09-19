package com.krish.ticket_booking.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record TheaterRegisterRequest(
        @NotBlank String name,
        @NotBlank String location,
        UUID managerId
) {}
