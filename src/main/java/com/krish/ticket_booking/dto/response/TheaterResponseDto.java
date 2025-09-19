package com.krish.ticket_booking.dto.response;

import java.util.UUID;

public record TheaterResponseDto(
        UUID id,
        String name,
        String location
) {}

