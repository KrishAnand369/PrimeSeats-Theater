package com.krish.ticket_booking.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ShowRegisterRequestDto(
        Double extraCharge, LocalDateTime startTime, UUID movieId
) {
}
