package com.krish.ticket_booking.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShowRegisterRequestDto(
        Double extraCharge, LocalDateTime startTime, UUID movieId
) {
}
