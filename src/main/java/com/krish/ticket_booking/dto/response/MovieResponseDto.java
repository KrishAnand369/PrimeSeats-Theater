package com.krish.ticket_booking.dto.response;

import java.util.UUID;

public record MovieResponseDto(
        UUID id,
        String title,
        String genre,
        String language,
        String posterUrl,
        int duration,
        boolean active
) {}
