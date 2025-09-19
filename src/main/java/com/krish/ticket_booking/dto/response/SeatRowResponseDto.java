package com.krish.ticket_booking.dto.response;

import com.krish.ticket_booking.dto.request.SeatRequestDto;

import java.util.List;
import java.util.UUID;

public record SeatRowResponseDto(
        UUID id,
        String rowLabel,
        List<SeatResponseDto> seats
) {
}
