package com.krish.ticket_booking.dto.response;

import com.krish.ticket_booking.entity.enums.LayoutType;

import java.util.List;
import java.util.UUID;

public record SeatLayoutResponseDto(
        UUID id,
        LayoutType layoutType,
        List<SeatRowResponseDto> rows
) {
}
