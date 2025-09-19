package com.krish.ticket_booking.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ShowLayoutResponseDto(
        UUID showId,
        String movieTitle,
        LocalDateTime startTime,
        List<ShowRowResponseDto> rows
) {
}
