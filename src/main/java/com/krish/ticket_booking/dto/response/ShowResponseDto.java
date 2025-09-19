package com.krish.ticket_booking.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShowResponseDto(
        UUID id,double extraCharge, LocalDateTime startTime, UUID movieId,
        UUID screenId
) {
}
